#!/bin/bash
echo "Checking if Kafka is running on 9092..."
if ! nc -z localhost 9092; then
  echo "Kafka is NOT reachable on localhost:9092"
  exit 1
fi
echo "Kafka is reachable."

echo "Starting Spring Boot application..."
./mvnw spring-boot:run > app.log 2>&1 &
APP_PID=$!

count=0
while ! grep -q "Started .* in .* seconds" app.log; do
  sleep 2
  count=$((count+1))
  if [ $count -ge 30 ]; then
     echo "Timeout waiting for app to start. Showing last 50 lines of app.log:"
     tail -n 50 app.log
     kill -9 $APP_PID
     exit 1
  fi
  # Also check if process died
  if ! kill -0 $APP_PID 2>/dev/null; then
     echo "App process died. Showing app.log:"
     cat app.log
     exit 1
  fi
done

echo "App started! Sending test POST request..."
curl -s -v -X POST http://localhost:8080/orders \
-H "Content-Type: application/json" \
-d '{"product": "MacBook Pro", "quantity": 2}'

echo -e "\nWaiting for messages to be processed..."
sleep 3

echo "=== APP LOGS RESULT ==="
grep -E "NotificationConsumer|InventoryConsumer|OrderProducer|Failed|Exception" app.log

echo "Killing application..."
kill -9 $APP_PID
