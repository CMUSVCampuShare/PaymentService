package com.campushare.PaymentService.service;

import com.campushare.PaymentService.dto.UsersRelatedPaymentDTO;
import com.campushare.PaymentService.dto.User;
import com.campushare.PaymentService.repository.AccessTokenRepository;
import com.campushare.PaymentService.repository.PaymentRepository;
import com.campushare.PaymentService.model.Payment;
import com.campushare.PaymentService.model.AccessTokenModel;
import com.campushare.PaymentService.exception.CannotGetUserException;
import com.campushare.PaymentService.exception.OrderNotFoundException;

import java.util.UUID;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    AccessTokenRepository accessTokenRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    PaypalSandbox paypalSandbox = new PaypalSandbox();


    public void createPayment(UsersRelatedPaymentDTO usersRelatedPaymentDTO) throws CannotGetUserException {
        String driverPaypalId = getPayPalId(usersRelatedPaymentDTO.getDriverId());
        int numberOfPassengers = usersRelatedPaymentDTO.getPassengerIds().length;
        String[] passengerPaypalIds = new String[numberOfPassengers];
        for (int i = 0; i < numberOfPassengers; i++) {
            passengerPaypalIds[i] = getPayPalId(usersRelatedPaymentDTO.getPassengerIds()[i]);

//          TODO:  String capturePaymentResponse = paypalSandbox.capturePayment(accessToken, authorizationId);
            Payment payment = new Payment(UUID.randomUUID().toString(), driverPaypalId, passengerPaypalIds[i], new Date(), 2.00);
            paymentRepository.save(payment);
        }
    }


    public String getPayPalId(String userId) throws CannotGetUserException {
        String userServiceUrl = "http://localhost:8081/users/" + userId; // TODO: change to the URL of the UserService
        ResponseEntity<User> response = restTemplate.getForEntity(userServiceUrl, User.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            User user = response.getBody();
            return user.getAccount();
        } else {
            throw new CannotGetUserException("Cannot get the user: " + response);
        }
    }

    public void saveAccessTokenToDB(String orderId, String rideId, String passengerId, String accessToken) {
        AccessTokenModel accessTokenModel = new AccessTokenModel(orderId, rideId, passengerId, accessToken, null, null);
        accessTokenRepository.save(accessTokenModel);
    }

    public String findAccessToken(String rideId, String passengerId) {
        AccessTokenModel accessTokenModel = accessTokenRepository.findByRideIdAndPassengerId(rideId, passengerId);
        return accessTokenModel != null ? accessTokenModel.getAccessToken() : null;
    }

    public String findOrderId(String rideId, String passengerId) {
        AccessTokenModel accessTokenModel = accessTokenRepository.findByRideIdAndPassengerId(rideId, passengerId);
        return accessTokenModel != null ? accessTokenModel.getOrderId() : null;
    }

    public void addAuthorizationId(String rideId, String passengerId, String authorizationId) throws OrderNotFoundException {
        AccessTokenModel accessTokenModel = accessTokenRepository.findByRideIdAndPassengerId(rideId, passengerId);
        if (accessTokenModel != null) {
            accessTokenModel.setAuthorizationId(authorizationId);
            accessTokenRepository.save(accessTokenModel);
        } else {
            throw new OrderNotFoundException("Cannot find the order related to rideId: " + rideId + ", and passengerId: " + passengerId);
        }
    }

    public String findAuthorizationId(String rideId, String passengerId) {
        AccessTokenModel accessTokenModel = accessTokenRepository.findByRideIdAndPassengerId(rideId, passengerId);
        return accessTokenModel != null ? accessTokenModel.getAuthorizationId() : null;
    }

    public void addCapturePaymentResponse(String rideId, String passengerId, String capturePaymentResponse) throws OrderNotFoundException {
        AccessTokenModel accessTokenModel = accessTokenRepository.findByRideIdAndPassengerId(rideId, passengerId);
        if (accessTokenModel != null) {
            accessTokenModel.setCapturePaymentResponse(capturePaymentResponse);
            accessTokenRepository.save(accessTokenModel);
        } else {
            throw new OrderNotFoundException("Cannot find the order related to rideId: " + rideId + ", and passengerId: " + passengerId);
        }
    }

}
