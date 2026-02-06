# AGENTS

Notes for automation/AI agents working on this repository.

## Stack
- Spring Boot 3.5.x
- Java 17
- Maven wrapper (`mvnw`, `mvnw.cmd`)
- PostgreSQL

## Run / test
- Windows:
  - Run: `.\\mvnw.cmd spring-boot:run`
  - Test: `.\\mvnw.cmd test`
- macOS/Linux:
  - Run: `./mvnw spring-boot:run`
  - Test: `./mvnw test`

## Configuration
- VS Code launch loads `.env` (see `.vscode/launch.json`)
- `src/main/resources/application.properties` uses env vars:
  - `DB_URL`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`

## Project structure (by convention)
- `src/main/java/com/cloud_technological/el_aventurero/`
  - `controller/`: REST endpoints (keep thin)
  - `services/`: service interfaces
  - `services/implementations/`: business logic
  - `repositories/`: JPA + query repositories
  - `dto/`: request/response models
  - `entity/`: JPA entities
  - `security/`, `config/`, `util/`: cross-cutting concerns

## Coding rules
- Controllers: validate/orchestrate only; no business logic.
- Prefer DTOs in API contracts; do not expose entities directly.
- Keep changes small and scoped; avoid refactors/formatting unrelated files.
- If behavior changes, add/update tests under `src/test/java/...`.

## Security / secrets
- Do NOT add or commit secrets (.env, passwords, tokens, JWT/AES keys).
- Do NOT log sensitive values (DB creds, JWT secret, AES key).
- Treat any secrets found in `application.properties` as sensitive; do not propagate them into docs or code.

## Dependency notes
- Swagger uses Springfox (`io.springfox:springfox-swagger2:2.9.2`).
  - Avoid dependency upgrades/migrations unless explicitly requested.
