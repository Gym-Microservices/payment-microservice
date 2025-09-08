package com.gym.payment_microservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "payments")
@Schema(description = "Entity that represents a gym payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the payment", example = "1")
    private Long id;

    @Column(name = "member_id", nullable = false)
    @Schema(description = "ID of the member making the payment", example = "1", required = true)
    private Long memberId;

    @Column(nullable = false)
    @Schema(description = "Payment amount", example = "50.00", required = true)
    private Double amount;

    @Column(name = "payment_date")
    @Schema(description = "Payment date", example = "2024-01-15T10:00:00Z")
    private Date paymentDate;
}
