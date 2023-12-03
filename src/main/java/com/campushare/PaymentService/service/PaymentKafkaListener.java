package com.campushare.PaymentService.service;

import com.campushare.PaymentService.dto.UsersRelatedPaymentDTO;
import com.campushare.PaymentService.exception.OrderNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentKafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(PaymentKafkaListener.class);
    @Autowired
    private PaymentService paymentService;

    @KafkaListener(topics = "user_payment_topic", groupId = "payment-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenUserPaymentTopic(String usersRelatedPaymentDTOString) throws JsonProcessingException, OrderNotFoundException {
        logger.info("Received a user_payment_topic message from Kafka: {}", usersRelatedPaymentDTOString);
        ObjectMapper objectMapper = new ObjectMapper();
        UsersRelatedPaymentDTO usersRelatedPaymentDTO = objectMapper.readValue(usersRelatedPaymentDTOString, UsersRelatedPaymentDTO.class);
        paymentService.createPayment(usersRelatedPaymentDTO);
    }
}
