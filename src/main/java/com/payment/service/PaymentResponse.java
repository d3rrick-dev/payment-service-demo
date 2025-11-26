package com.payment.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    String paymentId;
    Long userId;
    Long amount;
    String currency;
    String method;
    String status;
    String metadata;
}