package com.gym.payment_microservice.controller;

import com.gym.payment_microservice.model.Payment;
import com.gym.payment_microservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public String processPayment(@RequestBody Payment payment) {
        return paymentService.sendPayment(payment);
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all payments", description = "Returns a list of all payments")
    @ApiResponse(responseCode = "200", description = "List of payments retrieved successfully")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get payments by member", description = "Returns all payments made by a specific member")
    @ApiResponse(responseCode = "200", description = "Member's payment list retrieved successfully")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<List<Payment>> getPaymentsByMember(
            @Parameter(description = "Member ID") @PathVariable Long memberId) {
        List<Payment> payments = paymentService.getPaymentsByMember(memberId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Returns a specific payment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COACH') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<Payment> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
