package com.payment.repository;

import com.payment.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, String> {
    Optional<Payment> findByPaymentId(String paymentId);
}