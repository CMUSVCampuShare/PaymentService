package com.campushare.PaymentService.service;
import static org.mockito.Mockito.*;

import com.campushare.PaymentService.dto.UsersRelatedPaymentDTO;
import com.campushare.PaymentService.exception.OrderNotFoundException;
import com.campushare.PaymentService.model.AccessTokenModel;
import com.campushare.PaymentService.repository.AccessTokenRepository;
import com.campushare.PaymentService.repository.PaymentRepository;
import com.campushare.PaymentService.service.PaymentService;
import com.campushare.PaymentService.service.PaypalSandbox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

public class PaymentServiceTests {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PaypalSandbox paypalSandbox;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    AccessTokenRepository accessTokenRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreatePayment_createsPayment() throws OrderNotFoundException {
        String rideId = "rideId";
        String driverId = "driverId";
        String[] passengerIds = new String[1];
        passengerIds[0] = "passenger1";
        UsersRelatedPaymentDTO mockUserRelatedPaymentDTO = new UsersRelatedPaymentDTO(rideId, driverId, passengerIds);
        String capturePaymentResponse = "capturePaymentResponse";

        String mockAccessToken = "mockAccessToken";
        String mockAuthorizationId = "mockAuthorizationId";

        AccessTokenModel mockAccessTokenModel = new AccessTokenModel("orderId", rideId, driverId, mockAccessToken, mockAuthorizationId, capturePaymentResponse);


        when(accessTokenRepository.findByRideIdAndPassengerId(anyString(), anyString())).thenReturn(mockAccessTokenModel);
        when(paypalSandbox.capturePayment(anyString(), anyString())).thenReturn(capturePaymentResponse);
        when(paymentRepository.save(any())).thenReturn(null);

        paymentService.createPayment(mockUserRelatedPaymentDTO);
        verify(accessTokenRepository, times(3)).findByRideIdAndPassengerId(anyString(), anyString());
        verify(paypalSandbox, times(1)).capturePayment(anyString(), anyString());
        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    void addCapturePaymentResponse_capturesResponse() throws OrderNotFoundException {
        String rideId = "rideId";
        String driverId = "driverId";
        String passengerId = "passengerId";
        String capturePaymentResponse = "capturePaymentResponse";

        String mockAccessToken = "mockAccessToken";
        String mockAuthorizationId = "mockAuthorizationId";

        AccessTokenModel mockAccessTokenModel = new AccessTokenModel("orderId", rideId, driverId, mockAccessToken, mockAuthorizationId, capturePaymentResponse);

        when(accessTokenRepository.findByRideIdAndPassengerId(rideId, passengerId)).thenReturn(mockAccessTokenModel);
        when(accessTokenRepository.save(any())).thenReturn(null);

        paymentService.addCapturePaymentResponse(rideId, passengerId, capturePaymentResponse);

        verify(accessTokenRepository, times(1)).findByRideIdAndPassengerId(rideId, passengerId);
        verify(accessTokenRepository, times(1)).save(any());
    }

}
