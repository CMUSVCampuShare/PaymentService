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
    public String getAccessToken(@RequestParam String authorizationCode) throws JsonProcessingException {
        String accessToken = paypalSandbox.getPayPalAccessToken(authorizationCode);
        return accessToken;
    }

}
