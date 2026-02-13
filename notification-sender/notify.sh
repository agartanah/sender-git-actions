#!/bin/bash

SERVER_URL=$1
EVENT_TYPE=$2
REPO_NAME=$3
AUTHOR=$4
MESSAGE=$5
URL=$6

PAYLOAD='{
  "repositoryName": "'"$REPO_NAME"'",
  "author": "'"$AUTHOR"'",
  "eventMessage": "'"$MESSAGE"'",
  "eventUrl": "'"$URL"'"
}'

echo "Sending notification type: $EVENT_TYPE to $SERVER_URL"

RESPONSE=$(curl -s -X POST "$SERVER_URL/webhook/cicd" \
  -H "Content-Type: application/json" \
  -H "CI-Event-Type: $EVENT_TYPE" \
  -d "$PAYLOAD")

echo "Server Response: $RESPONSE"

if [[ $RESPONSE == *"Event accepted"* ]]; then
    echo "Notification successful!"
    exit 0
else
    echo "Notification failed!"
    exit 1
fi