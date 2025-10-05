package com.gym.payment_microservice.service;

import com.gym.payment_microservice.model.Payment;
import com.gym.payment_microservice.repository.PaymentRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private PaymentRepository paymentRepository;

    public String sendPayment(Payment payment){
        // Guardar el pago en la base de datos
        paymentRepository.save(payment);
        
        // Enviar a la cola
        rabbitTemplate.convertAndSend("pagos-exchange", "pagos-queue", payment);
        return "Payment sent to the queue successfully";
    }
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getPaymentsByMember(Long memberId) {
        return paymentRepository.findByMemberId(memberId);
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

}
