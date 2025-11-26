Client (frontend / mobile)
    └─ HTTP → Payment API (Spring Boot)
             ├─ Idempotency handling (DB / Redis)
             ├─ Persist Payment (MySQL)
             ├─ Push payment job to queue (RabbitMQ)
             ├─ Async worker / consumer processes payment (3rd-party mock)
             │     ├─ Calls Supplier Payment Provider (HTTP)
             │     └─ Emits webhook or callback to /webhooks/provider
             ├─ On success/failure: update DB, emit events, notify client (websocket/email)
             └─ Webhook endpoint (/webhooks/*) verifies signature & updates status