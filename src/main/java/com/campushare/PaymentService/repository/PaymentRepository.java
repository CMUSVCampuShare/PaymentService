package com.campushare.PaymentService.repository;

import com.campushare.PaymentService.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
}
