package com.campushare.PaymentService.service;

import com.campushare.PaymentService.controller.PaymentController;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class PaypalSandbox {
    private static final Logger logger = LoggerFactory.getLogger(PaypalSandbox.class);
    private RestTemplate restTemplate = new RestTemplate();
    @Value("${paypal.client.id}")
    private String CLIENT_ID;

    @Value("${paypal.client.secret}")
    private String CLIENT_SECRET;

    @Value("${paypal.paypal.oauth.url}")
    private String PAYPAL_OAUTH_URL;

    public String getPayPalAccessToken(String authorizationCode) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(PAYPAL_OAUTH_URL, request, String.class);

        logger.info("The response is: " + response);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        String accessToken = rootNode.path("access_token").asText();
        return accessToken;
    }
}
