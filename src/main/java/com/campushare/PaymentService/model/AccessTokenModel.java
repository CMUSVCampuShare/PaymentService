package com.campushare.PaymentService.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accessTokens")
@CompoundIndex(def = "{'rideId': 1, 'passengerId': 1}", unique = true)
@Data
@AllArgsConstructor
public class AccessTokenModel {
    @Id
    private String orderId;
    private String rideId;
    private String passengerId;
    private String accessToken;
    private String authorizationId;
    private String capturePaymentResponse;
}
