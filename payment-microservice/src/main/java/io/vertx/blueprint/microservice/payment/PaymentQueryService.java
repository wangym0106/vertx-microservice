package io.vertx.blueprint.microservice.payment;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface PaymentQueryService {

    /**
     * The name of the event bus service.
     */
    String SERVICE_NAME = "payment-query-eb-service";

    /**
     * The address on which the service is published.
     */
    String SERVICE_ADDRESS = "service.payment.query";

    /**
     * Initialize the persistence.
     *
     * @param resultHandler the result handler will be called as soon as the initialization has been accomplished. The async result indicates
     *                      whether the operation was successful or not.
     */
    void initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    /**
     * Add a payment record into the backend persistence.
     *
     * @param payment       payment entity
     * @param resultHandler async result handler
     */
    void addPaymentRecord(Payment payment, Handler<AsyncResult<Void>> resultHandler);

    /**
     * Retrieve payment record from backend by payment id.
     *
     * @param payId         payment id
     * @param resultHandler async result handler
     */
    void retrievePaymentRecord(String payId, Handler<AsyncResult<Payment>> resultHandler);
}
