#!/bin/bash

set -e  # stop the script on first error

APP_NAME="rencanakan-talentpool-api"

echo "=============================="
echo "🚀 Building Spring Boot JAR..."
echo "=============================="

./gradlew clean bootJar -x test

echo "=============================="
echo "🐳 Building Docker image..."
echo "=============================="

docker build -t $APP_NAME .

echo "=============================="
echo "🔄 Restarting Docker containers..."
echo "=============================="

docker compose up -d --build

echo "=============================="
echo "✅ Deployment complete!"
echo "=============================="