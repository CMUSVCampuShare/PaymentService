package com.campushare.PaymentService.dto;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UsersRelatedPaymentDTO {
    private String driverId;
    private String[] passengerIds;
}
