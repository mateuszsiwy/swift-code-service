# SwiftCode Service

A service that enables the management and search of bank SWIFT codes. The application loads data from a public Google Sheets document and provides a REST API for interacting with this data.

## Security Notice

This repository includes configuration files such as `application.properties` and `docker-compose.yml`, which, under normal production conditions, should not be publicly available. However, I have decided to include them to make it easier for testers to quickly and seamlessly run the application. The application runs locally, and the Google Sheets document used is public.

In a real production environment, these files should be replaced with environment variables or external configuration systems, and sensitive data such as passwords and API keys should be stored securely.


## Project Description

SwiftCode Service is a Spring Boot application that:
- Manages a database of bank SWIFT codes
- Stores relationships between bank headquarters and branches
- Allows searching for SWIFT codes by country or directly by code
- Provides a REST API for retrieving and managing data

## Requirements

- Java 21
- Docker and Docker Compose
- PostgreSQL (run via Docker)
- Maven (optional - you can use the built-in `mvnw` wrapper)

## Running the Application

### Using a Script

**On Unix/Linux/macOS systems:**
```
./run.sh
```
If above ends in error run the command below and then ./run.sh again.
```
sed -i 's/\r$//' run.sh
```

**On Windows systems:**
```
./run.bat
```

### Manual Execution

1. Build the project:
```
./mvnw clean package -DskipTests
```

2. Start the Docker containers:
```
docker-compose up -d --build
```

3. The application will be available at: http://localhost:8080

## API Endpoints

### 1. Retrieve SWIFT Code Details

```
GET /v1/swift-codes/{swiftCode}
```

**Parameters:**
- `swiftCode` - The bank's SWIFT code (e.g., PKOPPLPWXXX)

**Successful Response (200 OK):**
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
            "isHeadquarter": false
        }
    ],
    "isHeadquarter": true
}
```

**Error Response (404 Not Found)** - when the SWIFT code does not exist

### 2. Retrieve SWIFT Codes for a Country

```
GET /v1/swift-codes/country/{countryISO2}
```

**Parameters:**
- `countryISO2` - Two-letter ISO country code (e.g., PL, DE, US)

**Successful Response (200 OK):**
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
      "isHeadquarter": true
    },
    {
      "swiftCode": "PKOPPLPW",
      "bankName": "PKO BANK POLSKI ODDZIAŁ",
      "address": "ul. Jakaś 10, Gdańsk",
      "countryISO2": "PL",
      "countryName": "POLAND",
      "isHeadquarter": false
    }
  ]
}
```

**Error Response (404 Not Found)** - when no SWIFT codes exist for the given country

### 3. Add a New SWIFT Code

```
POST /v1/swift-codes
```

**Example Request Body:**
```json
{
  "swiftCode": "string",
  "bankName": "string",
  "address": "string",
  "countryISO2": "string",
  "countryName": "string",
  "isHeadquarter": bool
}
```

**Successful Response (200 OK):**
```
Swift code added successfully
```

**Error Response (500 Internal Server Error)** - in case of an error during addition

### 4. Delete a SWIFT Code

```
DELETE /v1/swift-codes/{swiftCode}
```

**Parameters:**
- `swiftCode` - The SWIFT code to delete

**Successful Response (200 OK):**
```
Swift code deleted successfully
```

**Error Response (400 Bad Request)** - when the code does not exist
**Error Response (500 Internal Server Error)** - in case of another error

## Project Structure

- **Entity**: SWIFT code data model
- **Repository**: Database access
- **Service**: Business logic
- **Controller**: REST API endpoints
- **DTO**: Data Transfer Objects
- **Loader**: Data loading from Google Sheets

## Configuration

The application uses the `application.properties` file for configuration. Main parameters:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/swiftcode
spring.datasource.username=postgres
spring.datasource.password=postgres
google.sheet.url=https://docs.google.com/spreadsheets/d/1iFFqsu_xruvVKzXAadAAlDBpIuU51v-pfIEU5HeGa8w
```

## Testing

The project includes integration and unit tests, which can be run using:
```
./mvnw test
```

## CI/CD

The project includes GitHub Actions configuration in the `.github/workflows/java-tests.yml` file, which automatically runs tests on every push and pull request.

