# CLAUDE.md

## Project

rag-assistant: an event-driven RAG platform, migrating from a single Spring Boot monolith to a set of Kafka-connected services. See `README.md` for the full narrative and thesis. See `docs/*-spec.md` for the current implementation spec of whichever service is being built.

## Stack and conventions

- Java 21, Spring Boot 3.5, Spring AI 1.1.7. Package root: `com.anfebule.rag_assistant.*`.
- Build and test: `./mvnw test`. Run: `./mvnw spring-boot:run`.
- Local infra: `docker compose up -d` brings up Postgres+pgvector, Ollama, and Kafka (KRaft mode, no Zookeeper).
- DTOs are Java records. Services use constructor injection. Follow the style already in `IngestionController.java`, `DocumentIngestionService.java`, and `QuestionAnsweringService.java` rather than introducing a different convention.

## Ground rules

- Don't introduce a new architectural pattern (a message broker feature, a new library, a new service) without stating the trade-off out loud first, the way `docs/*-spec.md` files do. If a spec looks wrong once you're implementing it, say so and why, don't quietly build around it.
- No Debezium, no CDC, anywhere in this repo. The polling outbox relay was a deliberate choice, see `docs/documents-service-spec.md` item 2, for the reasoning.
- Every new Kafka producer needs `enable.idempotence=true` and `acks=all`. Every new consumer needs to be safe to run twice on the same message.
- Write or update tests for anything you touch. `QuestionAnsweringControllerEvalTest.java` and `QuestionAnsweringControllerQualityEvalTest.java` show the existing pattern, retrieval correctness and LLM-as-judge answer quality, match that spirit for new test types.
- When you finish something on the README's "What's Next" list, update the checkbox and the Design Decisions section in the same change.

## Where decisions live

`docs/*-spec.md` files are the source of truth for each service's contracts. If a spec is silent on something, make a reasonable call, write your reasoning into the spec file so it stays current, and flag it in your summary. Don't improvise silently and leave no trace of the decision.