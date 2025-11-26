#!/bin/bash

URL="http://localhost:8080/api/payment"

CURRENCIES=("USD" "EUR" "KES" "GBP")
METHODS=("card" "mpesa" "paypal" "bank")

for i in {1..100}
do
  # Random values
  USER_ID=$(( RANDOM % 9000 + 1000 ))
  AMOUNT=$(( RANDOM % 5000 + 500 ))   # between 500 and 5500
  CURRENCY=${CURRENCIES[$RANDOM % ${#CURRENCIES[@]}]}
  METHOD=${METHODS[$RANDOM % ${#METHODS[@]}]}
  ORDER_ID="ORD-$(( RANDOM % 90000 + 10000 ))"

  # UUID for Idempotency Key
  IDEMPOTENCY_KEY=$(uuidgen)

  # JSON payload
  PAYLOAD=$(cat <<EOF
{
  "userId": $USER_ID,
  "amount": $AMOUNT,
  "currency": "$CURRENCY",
  "method": "$METHOD",
  "metadata": { "orderId": "$ORDER_ID" }
}
EOF
)

  echo "---- Sending request $i with Idempotency-Key: $IDEMPOTENCY_KEY ----"

  curl -s -X POST "$URL" \
    -H "Content-Type: application/json" \
    -H "Idempotency-Key: $IDEMPOTENCY_KEY" \
    -d "$PAYLOAD"

  echo -e "\n"
done