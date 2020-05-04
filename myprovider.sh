#!/bin/bash
set -e

PROGNAME=$(basename "$0")
REQUIRED_TOOLS="jq curl"

################################################################################
# Functions

die() {
    echo "$PROGNAME: $*" >&2
    exit 1
}

usage() {
    if [ "$*" != "" ]; then
        echo "Error: $*"
    fi

    cat << EOF
Usage: $PROGNAME [OPTION ...] [command]
Service Provider Utility

Commands:
new                 Register a new provider.
auth                Authenticate as a provider.
get-self            Get the provider profile.
update-self         Update the provider profile.
reset-password      Reset your provider password.
delete-self         Delete the provider.
list-services       List services.
new-service         Add a new service.
get-service         Get a service.
update-service      Update a service profile.
update-service-conn Update a service connection.
delete-service      Delete a service.
new-cert            Generate a new self-signed certificate.

Options:
-h, --help          Display this help text and exit.
-e, --env           Environment to use [edge].
EOF

    exit 1
}

check_tools() {
    for TOOL in $REQUIRED_TOOLS; do
        if ! command -v "$TOOL" > /dev/null; then
            echo "Could not find $TOOL! Exiting."
            exit 1
        fi
    done
}

check_auth() {
    if [ ! -f "./.cookie" ]; then
        echo "Not authenticated. Use the 'auth' command."
        exit 1
    fi
}

authenticate() {
    read -p "Local identifier [default: $zident]: " auth_ident
    if [ -z "$auth_ident" ]; then
        auth_ident="$zident"
    fi
    if [ ! -d "$auth_ident" ]; then
        echo "Invalid identifier. Directory not found: $auth_ident"
        exit 1
    fi
    auth_email=$(< "$auth_ident/.email")
    auth_password=$(< "$auth_ident/.password")
    resp=$(curl -s -X POST "$zapi/provider/login" \
        -H 'Content-Type: application/json' \
        -d '{"email":"'"$auth_email"'"
            ,"password":"'"$auth_password"'"}' \
        -c ./.cookie)
    if [ "$resp" != "" ]; then
        echo "$resp" | jq .
        exit 1
    fi
    echo "$auth_ident" > .current
    echo "Authenticated as $auth_email"
}

new_provider() {
    read -p "Local identifier [default: $zident]: " provider_ident
    if [ -z "$provider_ident" ]; then
        provider_ident="$zident"
    fi
    if [ -d "$provider_ident" ]; then
        echo "Directory exists: $provider_ident"
        exit 1
    fi

    read -p "Provider name: " provider_name
    read -p "Provider email: " provider_email
    read -p "Provider homepage: " provider_url
    read -p "Provider description: " provider_descr

    echo "Creating directory $provider_ident ..."
    mkdir "${provider_ident}"
    echo "Registering $provider_name ..."
    resp=$(curl -s -X POST "$zapi/provider/register" \
        -H 'Content-Type: application/json' \
        -d '{"name": "'"$provider_name"'",
             "email": "'"$provider_email"'",
             "url": "'"$provider_url"'",
             "description": "'"$provider_descr"'"
            }')
    echo "$resp"
    echo "$resp" | jq -r '.password' > "${provider_ident}/.password"
    echo "$provider_email" > "${provider_ident}/.email"
    echo "Done. Please check your e-mail."
}

get_self() {
    check_auth
    resp=$(curl -s -X GET "$zapi/provider" -b ./.cookie)
    if [[ "$resp" == \{* ]]; then
        echo "$resp" | jq .
    else
        echo "$resp"
    fi
}

update_self() {
    check_auth
    read -p "New provider name [default: no change]: " new_name
    read -p "New provider URL [default: no change]: " new_url
    read -p "New provider description [default: no change]: " new_descr
    if [ -z "$new_name" ]; then
        new_name="null"
    else
        new_name="\"$new_name\""
    fi
    if [ -z "$new_url" ]; then
        new_url="null"
    else
        new_url="\"$new_url\""
    fi
    if [ -z "$new_descr" ]; then
        new_descr="null"
    else
        new_descr="\"$new_descr\""
    fi
    echo "Updating provider profile ..."
    resp=$(curl -s -X PUT "$zapi/provider" \
        -H 'Content-Type: application/json' \
        -d '{
             "name": "'"$new_name"'",
             "url": "'"$new_url"'",
             "description": "'"$new_descr"'"
            }' \
        -b ./.cookie)
    if [ "$resp" != "" ]; then
        echo "$resp" | jq .
    fi
    echo "Done"
}

reset_password() {
    check_auth
    auth_ident=$(read_ident)
    echo "Requesting password reset ..."
    resp=$(curl -s -X POST "$zapi/provider/password-reset" \
        -H 'Content-Type: application/json' \
        -d '{"email": "'"$auth_ident"'"}')
    label=$(echo "$resp" | jq -r '.label')
    if [ "$resp" != "" ] && [ "$label" != "code-exists" ]; then
        echo "$resp" | jq .
        exit 1
    fi
    echo "You should have received an email."
    read -p "Received password reset key: " reset_key
    read -p "Received password reset code: " reset_code
    read -s -p "New password: " new_password
    echo
    echo "Completing password reset ..."
    resp=$(curl -s -X POST "$zapi/provider/password-reset/complete" \
        -H 'Content-Type: application/json' \
        -d '{
             "key": "'"$reset_key"'",
             "code": "'"$reset_code"'",
             "password": "'"$new_password"'"
            }')
    if [ "$resp" != "" ]; then
        echo "$resp" | jq .
        exit 1
    fi
    echo "Done"
}

delete_self() {
    check_auth
    auth_ident=$(read_ident)
    auth_password=$(read_password)
    read -p "Are you sure (yN)? " yn
    if [ "$yn" == "y" ] ; then
        echo "Deleting provider ..."
        curl -s -X DELETE "$zapi/provider" \
            -H 'Content-Type: application/json' \
            -d '{"password": "'"$auth_password"'"}' \
            -b ./.cookie
        rm -f ./.current
        rm -f ./.cookie
        rm -rf "$auth_ident"
        echo "Done"
    fi
}

new_service() {
    check_auth
    read -p "Service name: " service_name
    read -p "Service description: " service_descr
    read -p "Service summary: " service_summary
    read -p "Service base URL: " service_base_url
    read -p "Service RSA public key file: " service_pubkey_file
    read -p "Service tags (comma-separated): " service_tags_str

    service_pubkey=$(< "$service_pubkey_file")
    service_tags=$(echo "$service_tags_str" | sed 's/,/","/g')

    echo "Registering service $service_name ..."
    curl -s -X POST "$zapi/provider/services" \
        -H 'Content-Type: application/json' \
        -d '{"name": "'"$service_name"'",
             "description": "'"$service_descr"'",
             "summary": "'"$service_summary"'",
             "base_url": "'"$service_base_url"'",
             "public_key": "'"$service_pubkey"'",
             "tags": ["'"$service_tags"'"]
            }' \
        -b ./.cookie \
        | jq .
    echo "Done"
}

get_service() {
    check_auth
    read -p "Service ID: " service_id
    curl -s -X GET "$zapi/provider/services/$service_id" -b ./.cookie | jq .
}

list_services() {
    check_auth
    curl -s -X GET "$zapi/provider/services" -b ./.cookie | jq .
}

update_service() {
    check_auth
    read -p "Service ID: " service_id
    read -p "New service name [default: no change]: " new_name
    read -p "New service description [default: no change]: " new_descr
    read -p "New service tags [default: no change]: " new_tags
    if [ -z "$new_name" ]; then
        new_name="null"
    else
        new_name="\"$new_name\""
    fi
    if [ -z "$new_descr" ]; then
        new_descr="null"
    else
        new_descr="\"$new_descr\""
    fi
    if [ -z "$new_tags" ]; then
        new_tags="null"
    else
        new_tags_tmp=$(echo "$new_tags" | sed 's/,/","/g')
        new_tags="[\"$new_tags_tmp\"]"
    fi
    echo "Updating service profile ..."
    curl -s -X PUT "$zapi/provider/services/$service_id" \
        -H 'Content-Type: application/json' \
        -d '{"name": '"$new_name"',
             "description": '"$new_descr"',
             "tags": '"$new_tags"'
            }' \
        -b ./.cookie
    echo "Done"
}

update_service_conn() {
    check_auth
    read -p "Service ID: " service_id
    read -p "New service base URL [default: no change]: " new_base_url
    read -p "New service auth token [default: no change]: " new_auth_token
    read -p "New service public key (file) [default: no change]: " new_pubkey_file
    read -p "New service enabled status (true|false) [default: no change]: " new_enabled
    if [ -z "$new_base_url" ]; then
        new_base_url="null"
    else
        new_base_url="\"$new_base_url\""
    fi
    if [ -z "$new_auth_token" ]; then
        new_auth_tokens="null"
    else
        new_auth_tokens="[\"$new_auth_token\"]"
    fi
    if [ -z "$new_pubkey_file" ]; then
        new_pubkeys="null"
    else
        new_pubkey=$(< "$new_pubkey_file")
        new_pubkeys="[\"$new_pubkey\"]"
    fi
    if [ -z "$new_enabled" ]; then
        new_enabled="null"
    fi
    auth_password=$(read_password)
    echo "Updating service connection data ..."
    curl -s -X PUT "$zapi/provider/services/$service_id/connection" \
        -H 'Content-Type: application/json' \
        -d '{"base_url": '"$new_base_url"',
             "auth_tokens": '"$new_auth_tokens"',
             "public_keys": '"$new_pubkeys"',
             "enabled": '"$new_enabled"',
             "password": "'"$auth_password"'"
            }' \
        -b ./.cookie \
        | jq .
    echo "Done"
}

delete_service() {
    check_auth
    read -p "Service ID: " service_id
    auth_password=$(read_password)
    echo "Deleting service $service_id ..."
    curl -s -X DELETE "$zapi/provider/services/$service_id" \
        -H 'Content-Type: application/json' \
        -d '{"password": "'"$auth_password"'"}' \
        -b ./.cookie
    echo "Done"
}

new_cert() {
    read -p "Target directory: " cert_dir
    mkdir -p "$cert_dir"
    echo "Writing RSA key pair to $cert_dir/key.pem"
    openssl genrsa -out "$cert_dir/key.pem" 4096
    echo "Writing CSR to $cert_dir/csr.pem"
    openssl req -new -key "$cert_dir/key.pem" -out "$cert_dir/csr.pem"
    echo "Writing self-signed certificate to $cert_dir/cert.pem"
    openssl x509 -req -days 7300 -in "$cert_dir/csr.pem" -signkey "$cert_dir/key.pem" -out "$cert_dir/cert.pem"
    echo "Writing RSA public key to $cert_dir/pubkey.pem"
    openssl rsa -in "$cert_dir/key.pem" -pubout -out "$cert_dir/pubkey.pem"
}

read_ident() {
    cat ./.current
}

read_password() {
    auth_ident=$(< .current)
    cat "$auth_ident/.password"
}

################################################################################
# Program

check_tools

zident=$(whoami)
zcmd=""
zenv="prod"
zdomain="wire.com"
while [ $# -gt 0 ]; do
    case "$1" in
    -h|--help)
        usage
        ;;
    -e|--env)
        zenv="$2"
        shift
        ;;
    -*)
        usage "Unknown option '$1'"
        ;;
    *)
        if [ -z "$zcmd" ] ; then
            zcmd="$1"
        else
            usage "Too many arguments"
        fi
        ;;
    esac
    shift
done

if [ -z "$zcmd" ] ; then
    usage "Not enough arguments"
fi

zapi="https://${zenv}-nginz-https.${zdomain}"

case "$zcmd" in
    "new") new_provider ;;
    "auth") authenticate ;;
    "get-self") get_self ;;
    "update-self") update_self ;;
    "reset-password") reset_password ;;
    "delete-self") delete_self ;;
    "new-service") new_service ;;
    "list-services") list_services ;;
    "get-service") get_service ;;
    "update-service") update_service ;;
    "update-service-conn") update_service_conn ;;
    "delete-service") delete_service ;;
    "new-cert") new_cert ;;
    *) echo "Unknown command: $zcmd" ;;
esac

