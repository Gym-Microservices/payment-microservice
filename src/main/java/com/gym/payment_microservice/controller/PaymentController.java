package com.gym.payment_microservice.controller;

import com.gym.payment_microservice.model.Payment;
import com.gym.payment_microservice.service.PaymentService;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    //@PreAuthorize("hasAnyRole('ROLE_TRAINER', 'ROLE_ADMIN')")
    public String processPayment(@RequestBody Payment payment) {
        return paymentService.sendPayment(payment);
    }

}