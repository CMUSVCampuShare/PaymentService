package com.campushare.PaymentService.service;
import com.campushare.PaymentService.exception.OrderNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class PaymentKafkaListenerTests {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentKafkaListener paymentKafkaListener;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void listenUserPaymentTopic_readsFromKafka() throws OrderNotFoundException, JsonProcessingException {
        String jsonString = "{ \"rideId\": \"123\", \"driverId\": \"driver123\", \"passengerIds\": [\"passenger1\", \"passenger2\"] }";

        doNothing().when(paymentService).createPayment(any());

        paymentKafkaListener.listenUserPaymentTopic(jsonString);

        verify(paymentService, times(1)).createPayment(any());
    }
}
