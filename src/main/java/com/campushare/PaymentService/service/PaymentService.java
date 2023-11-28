package com.campushare.PaymentService.service;

import com.campushare.PaymentService.dto.UsersRelatedPaymentDTO;
import com.campushare.PaymentService.dto.User;
import com.campushare.PaymentService.repository.PaymentRepository;
import com.campushare.PaymentService.model.Payment;
import com.campushare.PaymentService.exception.CannotGetUserException;
import com.campushare.PaymentService.service.PaypalSandbox;

import java.util.UUID;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
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

//            String paymentResponse = paypalSandbox.createPaypalPayment(driverPaypalId, passengerPaypalIds[i], 5.23);

            Payment payment = new Payment(UUID.randomUUID().toString(), driverPaypalId, passengerPaypalIds[i], new Date(), 5.23);
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
}
