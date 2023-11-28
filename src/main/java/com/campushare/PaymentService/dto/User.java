package com.campushare.PaymentService.dto;

import com.campushare.PaymentService.dto.Role;
import com.campushare.PaymentService.dto.Schedule;
import com.campushare.PaymentService.dto.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String userId;
    private String username;
    private String password;
    private String email;
    private Role role;
    private Schedule schedule;
    private Address address;
    private String account;
    private Integer noOfSeats;
    private String licenseNo;

}
