package com.campushare.PaymentService.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersRelatedPaymentDTO {
    private String rideId;
    private String driverId;
    private String[] passengerIds;
}
