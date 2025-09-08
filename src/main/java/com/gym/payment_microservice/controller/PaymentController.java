package com.gym.payment_microservice.controller;

import com.gym.payment_microservice.model.Payment;
import com.gym.payment_microservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@Tag(name = "Payments", description = "API for gym payment processing")
@SecurityRequirement(name = "bearer-key")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process payment", description = "Processes a membership or service payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Error processing payment")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER') or hasRole('MEMBER')")
    public String processPayment(@RequestBody Payment payment) {
        return paymentService.sendPayment(payment);
    }
}
