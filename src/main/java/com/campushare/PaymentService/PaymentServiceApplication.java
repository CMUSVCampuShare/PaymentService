package com.campushare.PaymentService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication {

	public static void main(String[] args) {
		System.out.println("CampuShare PaymentServiceApplication started!");
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
