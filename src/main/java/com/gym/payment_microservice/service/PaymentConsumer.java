package com.gym.payment_microservice.service;

import com.gym.payment_microservice.model.Payment;
import com.gym.payment_microservice.repository.PaymentRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class PaymentConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final String MEMBER_SERVICE_URL = "http://member-microservice:8081";


    @RabbitListener(queues = "pagos-queue")
    public Payment processPayment(Payment payment) {

        Boolean memberExists = validateMemberExists(payment.getMemberId());

        if (!memberExists) {
            throw new AmqpRejectAndDontRequeueException("Error en el pago, enviando a DLQ", new IllegalArgumentException("El miembro no existe"));
        }

        if (payment.getPaymentDate() == null || payment.getPaymentDate().after(new Date())) {
            throw new AmqpRejectAndDontRequeueException("Fecha inválida en el pago", new IllegalArgumentException("Fecha no puede ser futura o nula"));
        }

        return paymentRepository.save(payment);

    }

    public boolean validateMemberExists(Long memberId) {
        return exists(MEMBER_SERVICE_URL + "/api/members/" + memberId);
    }

    private boolean exists(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            // Si devuelve 404 -> no existe
            return false;
        } catch (Exception e) {
            System.err.println("Error validando existencia en " + url + ": " + e.getMessage());
            return false;
        }
    }

}
