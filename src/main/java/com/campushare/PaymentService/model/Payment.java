package com.campushare.PaymentService.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Document(collection = "payment")
@Data
@AllArgsConstructor
public class Payment {
    @Id
    private String paymentId;
    private String moneyFrom;
    private String moneyTo;
    private Date timestamp;
    private double amount;
}
