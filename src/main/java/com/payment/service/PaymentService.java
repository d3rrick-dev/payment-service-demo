package com.payment.service;

import com.payment.controller.CreatePaymentRequest;
import com.payment.models.IdempotencyKey;
import com.payment.models.Payment;
import com.payment.repository.IdempodentKeyRepo;
import com.payment.repository.PaymentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final IdempodentKeyRepo idemRepo;
    private final PaymentRepo paymentRepo;
    private final RabbitTemplate rabbit;

    @Transactional
    @Cacheable(value = "idempotencyKeys", key = "#idemKey")
    public PaymentResponse createPayment(String idemKey, CreatePaymentRequest dto) {
        var mayBeExistingKey = idemRepo.findByKeyValue(idemKey);
        if (mayBeExistingKey.isPresent()) {
            var maybePayment = paymentRepo.findByPaymentId(mayBeExistingKey.get().getPaymentId());
            Assert.isTrue(maybePayment.isPresent(), "Payment not found for existing idempotent key");
            var existingPayment = maybePayment.get();
            log.info("Idempotent key found, returning existing payment with id {}", existingPayment.getPaymentId());
            return mapToResponse(existingPayment);
        } else {
            var payment = Payment.builder()
                    .paymentId("pmt_" + UUID.randomUUID())
                    .userId(dto.getUserId())
                    .amount(dto.getAmount())
                    .currency(dto.getCurrency())
                    .status("PENDING")
                    .method(dto.getMethod())
                    .metadata(dto.getMetadata())
                    .build();
            paymentRepo.save(payment);
            var key = IdempotencyKey.builder()
                    .keyValue(idemKey)
                    .paymentId(payment.getPaymentId())
                    .build();
            idemRepo.save(key);
            processPaymentAsync(payment.getPaymentId());
            log.info("Created payment with id {}", payment.getPaymentId());
            return mapToResponse(payment);
        }
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .metadata(payment.getMetadata())
                .build();
    }

    private void processPaymentAsync(String paymentId) {
            try {
                rabbit.convertAndSend("payments.process", paymentId);
            } catch (AmqpException e) {
                throw new RuntimeException(e);
            }
    }
}
