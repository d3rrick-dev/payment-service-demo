package com.payment.controller;

import com.payment.service.PaymentResponse;
import com.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    public final PaymentService paymentService;
    private final String IDEMPOTENCY_KEY = "Idempotency-Key";

    @PostMapping
    public PaymentResponse createPayment(
        @RequestHeader(IDEMPOTENCY_KEY) String idempotencyKey,
        @RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(idempotencyKey, request);
    }
}
