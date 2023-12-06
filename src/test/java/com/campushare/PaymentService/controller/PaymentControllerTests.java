package com.campushare.PaymentService.controller;

import com.campushare.PaymentService.exception.CannotGetUserException;
import com.campushare.PaymentService.exception.OrderNotFoundException;
import com.campushare.PaymentService.service.PaymentService;
import com.campushare.PaymentService.service.PaypalSandbox;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class PaymentControllerTests {
    @Mock
    private PaymentService paymentService;

    @Mock
    private PaypalSandbox paypalSandbox;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAccessTokenAndCreateOrder_callsPaypalSandboxAndPaymentService() throws CannotGetUserException, JsonProcessingException {
        String mockToken = "mockToken";
        String mockOrderId = "mockOrderId";
        String mockDriverId = "mockDriverId";
        String mockDriverPaypalId = "mockDriverPaypalId";
        String mockAuthorizationCode = "mockAuthorizationCode";
        String mockRideId = "mockRideId";
        String mockPassengerId = "mockPassengerId";

        when(paymentService.getPayPalId(mockDriverId)).thenReturn(mockDriverPaypalId);
        when(paypalSandbox.getPayPalAccessToken(mockAuthorizationCode, mockDriverPaypalId)).thenReturn(mockToken);
        when(paypalSandbox.createOrder(mockToken,mockDriverPaypalId)).thenReturn(mockOrderId);

        doNothing().when(paymentService).saveAccessTokenToDB(mockOrderId, mockRideId, mockPassengerId, mockToken);

        String expectedResponse = "https://www.sandbox.paypal.com/checkoutnow?token=" + mockOrderId;

        String actualResponse = paymentController.getAccessTokenAndCreateOrder(mockAuthorizationCode, mockDriverId, mockRideId, mockPassengerId);

        Assertions.assertEquals(expectedResponse, actualResponse);

        verify(paymentService, times(1)).getPayPalId(mockDriverId);
        verify(paypalSandbox, times(1)).getPayPalAccessToken(mockAuthorizationCode, mockDriverPaypalId);
        verify(paypalSandbox, times(1)).createOrder(mockToken,mockDriverPaypalId);
        verify(paymentService, times(1)).saveAccessTokenToDB(mockOrderId, mockRideId, mockPassengerId, mockToken);
    }

    @Test
    void testAuthorizeOrder_callPaymentServiceToAuthorizeOrder() throws JsonProcessingException, OrderNotFoundException {
        String mockRideId = "mockRideId";
        String mockPassengerId = "mockPassengerId";
        String mockToken = "mockToken";
        String mockOrderId = "mockOrderId";
        String mockAuthorizationId = "mockAuthorizationId";

        when(paymentService.findAccessToken(mockRideId, mockPassengerId)).thenReturn(mockToken);
        when(paymentService.findOrderId(mockRideId, mockPassengerId)).thenReturn(mockOrderId);
        when(paypalSandbox.authorizeOrder(mockToken, mockOrderId)).thenReturn(mockAuthorizationId);

        doNothing().when(paymentService).addAuthorizationId(mockRideId, mockPassengerId, mockAuthorizationId);

        String actualAuthorizationId = paymentController.authorizeOrder(mockRideId, mockPassengerId);

        Assertions.assertEquals(mockAuthorizationId, actualAuthorizationId);

        verify(paymentService, times(1)).findAccessToken(mockRideId, mockPassengerId);
        verify(paymentService, times(1)).findOrderId(mockRideId, mockPassengerId);
        verify(paypalSandbox, times(1)).authorizeOrder(mockToken, mockOrderId);
        verify(paymentService, times(1)).addAuthorizationId(mockRideId, mockPassengerId, mockAuthorizationId);
    }
}
