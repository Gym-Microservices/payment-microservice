package com.gym.payment_microservice.service;

import com.gym.payment_microservice.model.Payment;
import com.gym.payment_microservice.repository.PaymentRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Date;


@Service
public class PaymentConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final String MEMBER_SERVICE_URL = "http://member-microservice:8081";

    @RabbitListener(queues = "pagos-queue")
    public void processPayment(Payment payment) {

        // Validación de fecha de pago
        if (payment.getPaymentDate() == null || payment.getPaymentDate().before(new Date())) {
            throw new AmqpRejectAndDontRequeueException("Error en el pago, enviando a DLQ",
                    new IllegalArgumentException("La fecha de pago no es válida (debe ser igual o posterior a hoy)"));
        }

        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new AmqpRejectAndDontRequeueException("Error en el pago, enviando a DLQ",
                    new IllegalArgumentException("El monto debe ser mayor a 0"));
        }

        // Guardar el pago válido
        paymentRepository.save(payment);

        System.out.println("Payment processed and saved: " + payment.toString());
    }

    public boolean validateMemberExists(Long memberId) {
        return exists(MEMBER_SERVICE_URL + "/api/members/" + memberId);
    }

    private boolean exists(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Call", "true");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getStatusCode().is2xxSuccessful();
        
        } catch (Exception e) {
            System.err.println("Error validando existencia en " + url + ": " + e.getMessage());
            return false;
        }
    }
}

