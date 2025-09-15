package com.gym.payment_microservice.service;

import com.gym.payment_microservice.model.Payment;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String sendPayment(Payment payment){
        rabbitTemplate.convertAndSend("pagos-exchange", "pagos-queue", payment);
        return "Payment sent to the queue successfully";
    }

}
