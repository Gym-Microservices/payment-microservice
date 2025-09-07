package com.gym.payment_microservice.repository;

import com.gym.payment_microservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
