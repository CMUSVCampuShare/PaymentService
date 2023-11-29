package com.campushare.PaymentService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
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


    public String getPayPalAccessToken(String authorizationCode) throws JsonProcessingException {
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
        return accessToken;
    }
    public String createOrder(String accessToken) {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // 创建请求体
        String requestJson = "{"
                + "\"intent\": \"AUTHORIZE\","
                + "\"purchase_units\": ["
                + "  {"
                + "    \"amount\": {"
                + "      \"currency_code\": \"USD\","
                + "      \"value\": \"520.00\""
                + "    },"
                + "    \"payee\": {"
                + "      \"email_address\": \"sb-grwaw27301475@personal.example.com\""
                + "    }"
                + "  }"
                + "]"
                + "}";

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);


        // 发送POST请求
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api-m.sandbox.paypal.com/v2/checkout/orders",
                request,
                String.class
        );

        return response.toString();
    }

    public String authorizeOrder(String accessToken, String orderId) {


        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // 创建空的请求体
        HttpEntity<String> request = new HttpEntity<>("{}", headers);
        logger.info("🌊The request is: " + request);

        // 发送POST请求来授权订单
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/authorize",
                request,
                String.class
        );


        return response.toString();
    }

    public String capturePayment(String accessToken, String authorizationId) {
        logger.info("🌊The accessToken is: " + accessToken);
        logger.info("🌊The authorizationId is: " + authorizationId);
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // 创建空的请求体
        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        logger.info("🌊The request is: " + request);
        // 发送POST请求来捕获支付
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.sandbox.paypal.com/v2/payments/authorizations/" + authorizationId + "/capture",
                request,
                String.class
        );
        logger.info("🌊The response is: " + response);
        return response.toString();
    }


//    @Data
//    @AllArgsConstructor
//    public static class PaymentRequest {
//        private String payer_id;
//        private String receiver_id;
//        private double amount;
//    }

//    public String createPreAuthorizationPayment(String access_token, String payer_id, String receiver_id, double amount) throws JsonProcessingException {
//        logger.info("👌createPreAuthorizationPayment is called" +
//                ",\n access_token: " + access_token +
//                ",\n payer_id: " + payer_id +
//                ",\n receiver_id: " + receiver_id
//                + ",\n amount: " + amount);
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + access_token);
//        ObjectMapper objectMapper = new ObjectMapper();
//        PaymentRequest paymentRequest = new PaymentRequest(payer_id, receiver_id, amount);
//        String jsonRequest = objectMapper.writeValueAsString(paymentRequest);
//        HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);
//        logger.info("🦊The request of createPreAuthorizationPayment is: " + request);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                "https://api-m.sandbox.paypal.com/v2/payments/authorizations/create",
//                HttpMethod.POST,
//                request,
//                String.class
//        );
//        logger.info("🤣The response of getAuthorizationId is: " + response);
//        JsonNode responseObject = objectMapper.readTree(response.getBody());
//        String authorizationId = responseObject.path("id").asText();
//        logger.info("☁️authorizationId is: " + authorizationId);
//
//        return authorizationId;
//    }

//    public String getAuthorizationId(String access_token, double amount) throws JsonProcessingException {
//        logger.info("👌getAuthorizationId is called, the access_token is: " + access_token + ", amount: " + amount);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(access_token);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode paymentDetails = objectMapper.createObjectNode();
//        paymentDetails.put("intent", "AUTHORIZE");
//
//        ObjectNode amountNode = objectMapper.createObjectNode();
//        amountNode.put("currency_code", "USD");
//        amountNode.put("value", amount);
//        paymentDetails.set("amount", amountNode);
//
//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(paymentDetails), headers);
//
//        logger.info("🦊The request of getAuthorizationId is: " + request);
//
//        ResponseEntity<String> response = restTemplate.postForEntity("https://api-m.sandbox.paypal.com/v2/payments/authorizations/create", request, String.class);
//
//        logger.info("🤣The response of getAuthorizationId is: " + response);
//
//        JsonNode responseObject = objectMapper.readTree(response.getBody());
//        String authorizationId = responseObject.path("id").asText();
//        logger.info("☁️authorizationId is: " + authorizationId);
//        return authorizationId;
//    }
}
