# Documents Service, Design Spec v1

Front door for document ingestion. Accepts a document, durably records it, and reliably gets it onto Kafka for the rest of the pipeline. Owns no chunking or embedding logic, that happens downstream.

## Decisions already made

Do not relitigate these while implementing. If you think one is wrong, say so and why in your summary, do not just build around it silently.

1. **Dual-write problem -> Transactional Outbox.** No direct call to Kafka from the request thread. The accept endpoint does one local Postgres transaction only, then returns. A separate relay is responsible for actually getting the row to Kafka.
2. **Relay mechanism -> polling, not Debezium.** A scheduled job polls for unpublished rows and publishes them. Because more than one instance of this service may run, the polling query must use `SELECT ... FOR UPDATE SKIP LOCKED` so competing instances never grab the same row, no external coordination needed. No Kafka Connect, no CDC.
3. **Delivery guarantee -> idempotent producer + at-least-once delivery.** The Kafka producer must set `enable.idempotence=true` and `acks=all`, so retries cannot create duplicates on the broker. Delivery to consumers downstream is still at-least-once by nature, consumers are expected to be idempotent (already decided for the Chunk Process Service).
4. **HTTP contract -> 202, not 201.** `POST /documents` returns `202 Accepted` with a `Location` header pointing at a status resource (`/documents/{id}/status`), as soon as the local transaction commits. No blocking, no `CompletableFuture`, the request path is one fast DB write.
5. **Partitioning -> keyed by `customerId`, 10 partitions to start.** This buys per-customer ordering, not fairness, do not describe it as fairness in code comments or docs. Ten is a chosen starting point (up to 10 parallel consumers), not a Kafka default. Known limitation: increasing partitions later changes the key-to-partition mapping for existing customers going forward, this is not a free operation.
6. **Noisy-neighbor mitigation at this service's edge -> Resilience4j rate limiter, keyed by `customerId`.** Requests over the limit return `429 Too Many Requests`. This caps what enters the pipeline, it does not guarantee fair processing of what's already inside Kafka, that's a separate, deferred concern.
7. **Outbox shape -> folded into the `documents` table, not a separate outbox table.** The textbook pattern usually splits business state and outbox events into two tables. Here one row is one event (a document was received), so `documents` carries the business record and a `published_at` column doubles as the outbox marker. Flag it if you find a reason this stops holding (for example, if we need to publish more than one event per document later).
8. **Large documents -> explicitly out of scope for v1.** A document must fit in a single Kafka message today. The planned fix is the claim-check pattern (store in Postgres, publish a reference), not slicing into multiple messages. Do not build this now.
9. **Message key is decided (`customerId`). Message value schema is not, propose one.** Suggested starting fields: `documentId`, `customerId`, `title`, `content`, `schemaVersion`, `createdAt`. Flag it if raw content in the message looks impractical given typical document sizes, that tension is exactly what item 8 will eventually resolve.

## Non-goals for v1

Replay-on-demand, Debezium/CDC, a frontend, multi-tenant fairness beyond the edge rate limiter.

## Open items for whoever implements this

- Exact polling interval and batch size for the relay: pick something reasonable, make it configurable via `application.yml`, not hardcoded, and say what you picked and why.
- Resilience4j limiter numbers (permits per period, refresh period): propose defaults, externalize them, explain the reasoning.
- Kafka producer retry/backoff and `delivery.timeout.ms`: pick sensible values for a local dev setup, note where they'd need to change for anything beyond that.