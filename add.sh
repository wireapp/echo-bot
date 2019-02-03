#!/bin/bash

echo "=========================================="
echo "|        Script to add service           |"
echo "=========================================="

read -p "| E-mail....: " email
read -p "| Password..: " -s password

# Authenticate to Wire
resp=$(curl -s -XPOST "https://prod-nginz-https.wire.com/login" \
    -H 'Content-Type: application/json' \
    -d '{"email": "'"$email"'", "password": "'"$password"'"}')

token=$(echo "$resp" | jq -r ".access_token")
if [ token == "null" ]; then
	echo " "
	echo " "
	echo "Authentication error!!"
	echo " "
	exit 1
fi
echo ""
read -p "| Conversation name:" convname
if [ "${convname}" == "" ]; then
	echo " "
	echo "Missing conv name!"
	echo ""
	exit 1
fi
read -p "| Provider Id......: " provider
if [ "${provider}" == "" ]; then
	echo " "
	echo "Missing provider ID !"
	echo "Tip: You can find it running"
	echo "     myprovider.sh get-self"
	exit 1
fi

read -p "| Service Id.......: " service
if [ "${service}" == "" ]; then
	echo " "
	echo "Missing service ID !"
	echo "Tip: You can find it running"
	echo "     myprovider.sh list-services"
	exit 1
fi

# function needed to bypass curl problems with shell vars
build_data_conv()
{
 cat <<EOF
{
"users": [],"name":"${convname}"
}
EOF
}

#creating conv in wire
curl -s -XPOST "https://prod-nginz-https.wire.com/conversations" -H 'Content-Type: application/json' -H 'Authorization:Bearer '${token}'' -d "$(build_data_conv)" | jq -r '.id' > .conv

conv=$(cat .conv)

# function needed to bypass curl problems with shell vars
build_data_prov()
{
 cat <<EOF
{
"provider":"${provider}","service":"${service}"
}
EOF
}


# adding bot to the conv room
curl -i -XPOST 'https://prod-nginz-https.wire.com/conversations/'${conv}'/bots' -H 'Content-Type: application/json' -H 'Authorization:Bearer '${token}'' -d "$(build_data_prov)"

echo ""
echo "Done!"
