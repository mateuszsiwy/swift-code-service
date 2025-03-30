#!/bin/bash
echo "Uruchamianie SwiftCodeService..."

./mvnw clean package -DskipTests

docker-compose up -d --build

echo "Aplikacja dostepna pod adresem: http://localhost:8080"
