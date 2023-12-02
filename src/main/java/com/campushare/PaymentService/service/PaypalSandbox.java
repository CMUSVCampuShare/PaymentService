package com.campushare.PaymentService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class PaypalSandbox {
    private static final Logger logger = LoggerFactory.getLogger(PaypalSandbox.class);
    @Autowired
    private RestTemplate restTemplate;
    @Value("${paypal.client.id}")
    private String CLIENT_ID;

    @Value("${paypal.client.secret}")
    private String CLIENT_SECRET;

    @Value("${paypal.paypal.oauth.url}")
    private String PAYPAL_OAUTH_URL;

//    @Autowired
//    private  ObjectMapper objectMapper;

    public String getPayPalAccessToken(String authorizationCode, String driverPaypalId) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(PAYPAL_OAUTH_URL, request, String.class);
        logger.info("The response of getPayPalAccessToken is: " + response);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        String accessToken = rootNode.path("access_token").asText();
        logger.info("The accessToken is: " + accessToken);

        return accessToken;
    }

    public String createOrder(String accessToken, String driverPaypalId) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        String requestJson = "{"
                + "\"intent\": \"AUTHORIZE\","
                + "\"application_context\": {"
                + "  \"return_url\": \"http://127.0.0.1:3000/chat\"," // TODO: need to change to the frontend page indicating the authorization is done
                + "  \"cancel_url\": \"http://127.0.0.1:3000/profile\"" // TODO: need to change to the frontend page indicating the authorization is cancelled
                + "},"
                + "\"purchase_units\": [{"
                + "    \"amount\": {"
                + "      \"currency_code\": \"USD\","
                + "      \"value\": \"2.00\""
                + "    },"
                + "    \"payee\": {"
                + "      \"email_address\": \"" + driverPaypalId + "\""
                + "    }"
                + "  }"
                + "]}";

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://api-m.sandbox.paypal.com/v2/checkout/orders", request, String.class);
        logger.info("The response of createOrder is: " + response);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        String orderId = rootNode.path("id").asText();
        logger.info("The orderId is: " + orderId);

        return orderId;
    }

    public String authorizeOrder(String accessToken, String orderId) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>("{}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/authorize", request, String.class);
        logger.info("The response of authorizeOrder is: " + request);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        JsonNode purchaseUnitsNode = rootNode.path("purchase_units");
        JsonNode paymentsNode = purchaseUnitsNode.get(0).path("payments");
        JsonNode authorizationsNode = paymentsNode.path("authorizations");
        String authorizationId = authorizationsNode.get(0).path("id").asText();
        logger.info("The authorizationId is: " + authorizationId);

        return authorizationId;
    }

    public String capturePayment(String accessToken, String authorizationId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://api.sandbox.paypal.com/v2/payments/authorizations/" + authorizationId + "/capture", request, String.class);
        logger.info("The response of capturePayment is: " + response);

        return response.toString();
    }

}
