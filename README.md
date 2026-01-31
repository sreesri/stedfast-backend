# Stedfast

Spring Boot application with PostgreSQL.

## Prerequisites
- Java 21
- Docker & Docker Compose
- Maven (if running from command line without wrapper)

## Project Structure
- `src/main/java`: Source code
- `src/main/resources`: Configuration
- `docker-compose.yml`: Database configuration

## Running the Application
Since `spring-boot-docker-compose` is included, running the application should automatically start the PostgreSQL container.

```bash
# If you have maven installed
mvn spring-boot:run
```

## Database Access
- URL: `jdbc:postgresql://localhost:5432/stedfast`
- User: `stedfast`
- Pass: `stedfast`
