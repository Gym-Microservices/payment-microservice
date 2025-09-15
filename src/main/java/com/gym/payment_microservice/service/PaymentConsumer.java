package com.gym.payment_microservice.service;

import com.gym.payment_microservice.model.Payment;
import com.gym.payment_microservice.repository.PaymentRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class PaymentConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final String MEMBER_SERVICE_URL = "http://member-microservice:8081";

    @RabbitListener(queues = "pagos-queue")
    public void processPayment(Payment payment) {

        Boolean memberExists = validateMemberExists(payment.getMemberId());

        if (!memberExists) {
            throw new AmqpRejectAndDontRequeueException("Error en el pago, enviando a DLQ",
                    new IllegalArgumentException("El miembro no existe"));
        }

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

