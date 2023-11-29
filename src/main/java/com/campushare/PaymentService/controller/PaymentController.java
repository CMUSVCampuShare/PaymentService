package com.campushare.PaymentService.controller;

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
    private PaypalSandbox paypalSandbox;

    @PostMapping("/exchangeAccessCodeByAuthorizationCode")
    public String preAuthorization(@RequestParam String authorizationCode) throws JsonProcessingException {
        String accessToken = paypalSandbox.getPayPalAccessToken(authorizationCode);
        return accessToken;
    }

//    @PostMapping("/testAuthorizationId")
//    public String testAuthorizationId(@RequestParam String accessToken) throws JsonProcessingException {
//        String authorizationId = paypalSandbox.getAuthorizationId(accessToken, 2.00);
//        return authorizationId;
//    }
//
//    @PostMapping("/testCreatePreAuthorizationPayment")
//    public String testCreatePreAuthorizationPayment(@RequestParam String accessToken, @RequestParam String payer_id, @RequestParam String receiver_id, @RequestParam double amount) throws JsonProcessingException {
//        String authorizationId = paypalSandbox.createPreAuthorizationPayment(accessToken, payer_id, receiver_id, amount);
//        return authorizationId;
//    }

    @PostMapping("/createOrder")
    public String createOrder(@RequestParam String accessToken) {
        String response = paypalSandbox.createOrder(accessToken);
        return response;
    }

    @PostMapping("/authorizeOrder")
    public String authorizeOrder(@RequestParam String accessToken, @RequestParam String orderId) {
        String response = paypalSandbox.authorizeOrder(accessToken, orderId);
        return response;
    }

    @PostMapping("/capturePayment")
    public String capturePayment(@RequestParam String accessToken, @RequestParam String authorizationId) {
        String response = paypalSandbox.capturePayment(accessToken, authorizationId);
        return response;
    }


}
