# SwiftCode Service

Serwis umożliwiający zarządzanie i wyszukiwanie kodów SWIFT banków. Aplikacja ładuje dane z publicznego arkusza Google Sheets i udostępnia API REST do interakcji z tymi danymi.

## Opis projektu

SwiftCode Service to aplikacja Spring Boot, która:
- Zarządza bazą danych kodów SWIFT banków
- Przechowuje relacje pomiędzy centralami i oddziałami banków
- Umożliwia wyszukiwanie kodów SWIFT według kraju lub bezpośrednio po kodzie
- Dostarcza API REST do pobierania i zarządzania danymi

## Uwaga dotycząca bezpieczeństwa

W tym repozytorium zamieszczone zostały pliki konfiguracyjne, takie jak `application.properties` oraz `docker-compose.yml`, które w normalnych warunkach produkcyjnych nie powinny być publikowane. Zdecydowałem się je udostępnić, aby ułatwić osobom testującym szybkie i bezproblemowe uruchomienie aplikacji. Aplikacja działa wyłącznie lokalnie, a używany Google Sheets jest publiczny.

W prawdziwym środowisku produkcyjnym pliki te powinny zostać zastąpione przez zmienne środowiskowe lub zewnętrzne systemy konfiguracyjne, a wrażliwe dane takie jak hasła i klucze API powinny być przechowywane w bezpieczny sposób.

## Wymagania

- Java 21
- Docker i Docker Compose
- PostgreSQL (uruchamiany przez Docker)
- Maven (opcjonalnie - można użyć wbudowanego wrappera mvnw)

## Uruchomienie aplikacji

### Za pomocą skryptu

**Na systemach Unix/Linux/macOS:**
```
./run.sh
```

**Na systemach Windows:**
```
./run.bat
```

### Ręczne uruchomienie

1. Zbuduj projekt:
```
./mvnw clean package -DskipTests
```

2. Uruchom kontenery Docker:
```
docker-compose up -d --build
```

3. Aplikacja będzie dostępna pod adresem: http://localhost:8080

## Endpointy API

### 1. Pobierz szczegóły kodu SWIFT

```
GET /v1/swift-codes/{swiftCode}
```

**Parametry:**
- `swiftCode` - Kod SWIFT banku (np. PKOPPLPWXXX)

**Odpowiedź sukcesu (200 OK):**
```json
{
    "swiftCode": "BIGBPLPWXXX",
    "bankName": "BANK MILLENNIUM S.A.",
    "address": "HARMONY CENTER UL. STANISLAWA ZARYNA 2A WARSZAWA, MAZOWIECKIE, 02-593",
    "countryISO2": "PL",
    "countryName": "POLAND",
    "branches": [
        {
            "swiftCode": "BIGBPLPWCUS",
            "bankName": "BANK MILLENNIUM S.A.",
            "address": "HARMONY CENTER UL. STANISLAWA ZARYNA 2A WARSZAWA, MAZOWIECKIE, 02-593",
            "countryISO2": "PL",
            "countryName": "POLAND",
            "headquarter": false
        }
    ],
    "headquarter": true
}
```

**Odpowiedź błędu (404 Not Found)** - gdy kod SWIFT nie istnieje

### 2. Pobierz kody SWIFT dla kraju

```
GET /v1/swift-codes/country/{countryISO2}
```

**Parametry:**
- `countryISO2` - Dwuliterowy kod ISO kraju (np. PL, DE, US)

**Odpowiedź sukcesu (200 OK):**
```json
{
  "countryISO2": "PL",
  "countryName": "POLAND",
  "branches": [
    {
      "swiftCode": "PKOPPLPWXXX",
      "bankName": "PKO BANK POLSKI",
      "address": "ul. Puławska 15, Warszawa",
      "countryISO2": "PL",
      "countryName": "POLAND",
      "headquarter": true
    },
    {
      "swiftCode": "PKOPPLPW",
      "bankName": "PKO BANK POLSKI ODDZIAŁ",
      "address": "ul. Jakaś 10, Gdańsk",
      "countryISO2": "PL",
      "countryName": "POLAND",
      "headquarter": false
    }
  ]
}
```

**Odpowiedź błędu (404 Not Found)** - gdy dla danego kraju nie ma kodów SWIFT

### 3. Dodaj nowy kod SWIFT

```
POST /v1/swift-codes
```

**Przykładowe ciało żądania:**
```json
{
  "swiftCode": "string",
  "bankName": "string",
  "address": "string",
  "countryISO2": "string",
  "countryName": "string",
  "headquarter": bool
}
```

**Odpowiedź sukcesu (200 OK):**
```
Swift code added successfully
```

**Odpowiedź błędu (500 Internal Server Error)** - w przypadku błędu podczas dodawania

### 4. Usuń kod SWIFT

```
DELETE /v1/swift-codes/{swiftCode}
```

**Parametry:**
- `swiftCode` - Kod SWIFT do usunięcia

**Odpowiedź sukcesu (200 OK):**
```
Swift code deleted successfully
```

**Odpowiedź błędu (400 Bad Request)** - gdy kod nie istnieje
**Odpowiedź błędu (500 Internal Server Error)** - w przypadku innego błędu

## Struktura projektu

- **Entity**: Model danych kodu SWIFT
- **Repository**: Dostęp do bazy danych
- **Service**: Logika biznesowa
- **Controller**: Endpointy REST API
- **DTO**: Obiekty transferu danych
- **Loader**: Ładowanie danych z Google Sheets

## Konfiguracja

Aplikacja wykorzystuje plik `application.properties` do konfiguracji. Główne parametry:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/swiftcode
spring.datasource.username=postgres
spring.datasource.password=postgres
google.sheet.url=https://docs.google.com/spreadsheets/d/1iFFqsu_xruvVKzXAadAAlDBpIuU51v-pfIEU5HeGa8w
```

## Testy

Projekt zawiera testy integracyjne i jednostkowe, które można uruchomić za pomocą:

```
./mvnw test
```


## CI/CD

Projekt zawiera konfigurację GitHub Actions w pliku `.github/workflows/java-tests.yml`, która automatycznie uruchamia testy przy każdym push i pull request.
