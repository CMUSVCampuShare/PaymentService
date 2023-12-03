package com.campushare.PaymentService.controller;

import com.campushare.PaymentService.exception.OrderNotFoundException;
import com.campushare.PaymentService.service.PaymentService;
import com.campushare.PaymentService.service.PaypalSandbox;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaypalSandbox paypalSandbox;


    @PostMapping("/getAccessTokenAndCreateOrder")
    public String getAccessTokenAndCreateOrder(@RequestParam String authorizationCode, @RequestParam String driverPaypalId, @RequestParam String rideId, @RequestParam String passengerId) throws JsonProcessingException {
        String accessToken = paypalSandbox.getPayPalAccessToken(authorizationCode, driverPaypalId);
        String orderId = paypalSandbox.createOrder(accessToken, driverPaypalId);
        paymentService.saveAccessTokenToDB(orderId, rideId, passengerId, accessToken);
        String orderAuthUrl = "https://www.sandbox.paypal.com/checkoutnow?token=" + orderId;
        return orderAuthUrl; // TODO: Frontend need to redirect the user to this orderAuthUrl
    }

    @PostMapping("/authorizeOrder")
    public String authorizeOrder(@RequestParam String rideId, @RequestParam String passengerId) throws JsonProcessingException, OrderNotFoundException {
        String accessToken = paymentService.findAccessToken(rideId, passengerId);
        String orderId = paymentService.findOrderId(rideId, passengerId);
        String authorizationId = paypalSandbox.authorizeOrder(accessToken, orderId);
        paymentService.addAuthorizationId(rideId, passengerId, authorizationId);
        return authorizationId;
    }

}
