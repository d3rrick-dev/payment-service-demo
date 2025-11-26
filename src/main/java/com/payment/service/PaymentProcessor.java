package com.payment.service;

import com.payment.repository.PaymentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessor {
    private final PaymentRepo paymentRepo;

    @RabbitListener(queues = "payments.process")
    public void handle(String paymentId) {

        try {
            var maybePayment = paymentRepo.findByPaymentId(paymentId);
            Assert.isTrue(maybePayment.isPresent(), "Payment not found for existing idempotent key");
            var existingPayment = maybePayment.get();
            // Business logic to process payment with external provider
            // if provider sync returns success -> update payment
            // else wait for webhook
            log.info("Processing payment with id {}", existingPayment.getPaymentId());
            var updatedPayment = existingPayment.toBuilder()
                    .status("PROCESSING").build();
            //  E.g a webhook (Stripe, Twilio, Paystack, etc.) X-Signature
            //  Normally has X-Signature header to  verify authenticity
            //  1. Take the JSON payload.
            //  2. Hash it with your secret key using SHA256/HMAC.
            //  3. Compare your hash with the incoming signature.

            paymentRepo.save(updatedPayment);

        } catch (Exception e) {
            // push to DLQ / retry mechanism
            log.error("Error processing payment with id {}: {}", paymentId, e.getMessage());
            throw e;
        }
    }
}