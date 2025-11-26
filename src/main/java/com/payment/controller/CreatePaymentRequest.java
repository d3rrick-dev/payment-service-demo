package com.payment.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    Long userId;
    Long amount;
    String currency;
    String method;
    Map<String, Object> metadata;

    public String getMetadata() {
        try {
            return new ObjectMapper().writeValueAsString(metadata);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert metadata to JSON", e);
        }
    }
}
