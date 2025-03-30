@echo off
echo Uruchamianie SwiftCodeService...

call mvnw clean package -DskipTests
docker-compose up -d --build

echo Aplikacja dostepna pod adresem: http://localhost:8080
pause