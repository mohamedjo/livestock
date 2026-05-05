# 🐄 Livestock Service

Spring Boot microservice for managing animals and their lifecycle, using DDD + CQRS + event-driven integration (Kafka).

## Tech stack
- Java 17
- Spring Boot
- Spring Web (MVC)
- Spring Data JPA
- PostgreSQL
- Flyway
- Apache Kafka
- Lombok

## Project structure (packages)
- `com.shabic.livestock.domain`: domain model + domain events
- `com.shabic.livestock.application`: CQRS commands/queries + handlers
- `com.shabic.livestock.infrastructure`: persistence (JPA) + messaging (Kafka)
- `com.shabic.livestock.api`: REST controllers + DTOs

## Local development

### 1) Start dependencies (Postgres + Kafka)

From repo root:

```bash
docker compose up -d
```

### 2) Configure environment (optional)

The service reads defaults from `src/main/resources/application.properties` and can be overridden via env vars:

- `DB_URL` (default `jdbc:postgresql://localhost:5432/livestock`)
- `DB_USERNAME` (default `livestock`)
- `DB_PASSWORD` (default `livestock`)
- `KAFKA_BOOTSTRAP_SERVERS` (default `localhost:9092`)
- `KAFKA_CONSUMER_GROUP_ID` (default `livestock-service`)
- Topics:
  - `KAFKA_TOPIC_ANIMAL_FED` (default `livestock.animal.fed`)
  - `KAFKA_TOPIC_ANIMAL_VACCINATED` (default `livestock.animal.vaccinated`)

### 3) Run the service

```bash
./gradlew bootRun
```

Flyway migrations run automatically on startup.

## Database

Migrations live in `src/main/resources/db/migration`.

Tables created:
- `animal`
- `animal_history` (audit/event log, `event_data` stored as JSONB)

## REST API

Base path: `/api/animals`

### Register animal (Command)
`POST /api/animals`

Request body:

```json
{
  "tagNumber": "TAG-001",
  "type": "cow",
  "breed": "holstein",
  "gender": "FEMALE",
  "birthDate": "2025-01-01",
  "farmId": "00000000-0000-0000-0000-000000000001",
  "initialLocationId": "00000000-0000-0000-0000-000000000010"
}
```

Response: `201 Created` with the created animal UUID.

### Get animals by farm (Query)
`GET /api/animals?farmId={farmId}`

### Get animal details (Query)
`GET /api/animals/{id}`

### Get animal history (Query)
`GET /api/animals/{id}/history`

## Events (Kafka)

The REST API does not publish domain events to Kafka in the current codebase. Incoming external messages are consumed for `animal_history` only.

### Consumed events (for audit/history)
- `AnimalFed` → topic `livestock.animal.fed`
- `AnimalVaccinated` → topic `livestock.animal.vaccinated`

The consumer stores them in `animal_history` if the payload contains `animalId`.

## Business rules implemented
- `Animal` is the aggregate root.
- Lifecycle transitions enforced:
  - Only `ALIVE` animals can be slaughtered or marked dead (domain methods; not exposed via REST in this service).
- External `AnimalFed` / `AnimalVaccinated` payloads are appended to `animal_history` when consumed from Kafka.

