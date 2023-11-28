# PaymentService

Payment Service of CampuShare
1. Listen to user_payment_topic from Kafka
2. Get related users' PayPal information from User Profile Service via REST API
3. Create a payment via calling PayPal API
4. If successful, save into the database
