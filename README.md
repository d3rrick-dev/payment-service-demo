# Payment Service (Spring Boot)

A sample Payment Service demonstrating:
- Idempotent POST /payments using Idempotency-Key
- Async processing with RabbitMQ (worker architecture)
- Secure webhook handling (HMAC signature verification)
- Reliable processing with retries and DLQ
- Persisted audit log of payment events (MySQL)

Tech: Java 25, Spring Boot, Spring Data JPA, RabbitMQ, MySQL, Redis (optional)

Quick start:
1. `docker-compose up -d`
2. `mvn spring-boot:run`
3. POST /api/payments (include header `Idempotency-Key`) -> returns paymentId
4. Simulate provider webhook: POST /api/webhooks/payment-provider with valid signature -> updates payment status