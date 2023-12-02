package com.campushare.PaymentService.repository;

import com.campushare.PaymentService.model.AccessTokenModel;
import com.campushare.PaymentService.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepository extends MongoRepository<AccessTokenModel, String> {
    AccessTokenModel findByRideIdAndPassengerId(String rideId, String passengerId);

}