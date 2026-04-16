# Hotel Service API

This is a Spring Boot REST API for managing hotels.  
The application allows creating, retrieving, searching, and updating hotel data, as well as working with amenities and statistical histograms.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 Database (in-memory)
- Liquibase
- Maven
- JUnit 5 + Mockito

---

## 📦 Features

- Create a hotel
- Get hotel by ID
- Get list of hotels (short view)
- Search hotels by filters:
  - name
  - brand
  - city
  - country
  - amenities
- Add amenities to existing hotel
- Get histogram statistics by:
  - brand
  - city
  - country
  - amenities
- Global exception handling
- Input validation (Bean Validation)
- OpenAPI / Swagger support (if enabled)

---

## 📁 API Base Path

---

## 🔌 Endpoints

### Get all hotels
GET /api/hotels

### Get hotel by ID
GET /api/hotels/{id}

### Create hotel
POST /api/hotels

### Search hotels
GET /api/hotels/search

### Add amenities
POST /api/hotels/{id}/amenities

### Delete hotel
DELETE /api/hotels/{id}

### Histogram
GET /api/hotels/histogram?param=brand|city|country|amenities

2. Run with Maven
mvn spring-boot:run

3. Run tests
mvn clean test

4. Access application
    API: http://localhost:8080/api/hotels
    H2 Console: http://localhost:8080/h2-console

H2 credentials:
    JDBC URL: jdbc:h2:mem:testdb
    Username: sa
    Password: (empty)

📖 API Documentation
If Swagger is enabled:
    http://localhost:8080/swagger-ui.html

🧪 Testing

The project includes:

Unit tests (service layer)
Controller tests (MockMvc)

To run tests:
    mvn test

⚠️ Notes
Database is in-memory (H2), so all data resets after restart.
Liquibase is used for database schema management.
Global exception handler returns structured error responses.

👨‍💻 Author:
    Dzianis Smolski