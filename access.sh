#!/bin/bash

echo "================================================"
echo "|  Script that requests access token from Wire |"
echo "================================================"

read -p "| E-mail....: " email
read -p "| Password..: " -s password

# authenticating in wire
resp=$(curl -s -XPOST "https://prod-nginz-https.wire.com/login" \
    -H 'Content-Type: application/json' \
    -d '{"email": "'"$email"'", "password": "'"$password"'"}')

token=$(echo "$resp" | jq -r ".access_token")

echo ""
echo "Access token:"
echo "$token"