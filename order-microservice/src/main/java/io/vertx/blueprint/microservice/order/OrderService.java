package io.vertx.blueprint.microservice.order;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

@ProxyGen
@VertxGen
public interface OrderService {


    String SERVICE_NAME = "order-storage-eb-service";

    String SERVICE_ADDRESS = "service.order.storage";

    @Fluent
    OrderService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    OrderService retrieveOrdersForAccount(String accountId, Handler<AsyncResult<List<Order>>> resultHandler);

    @Fluent
    OrderService createOrder(Order order, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    OrderService retrieveOrder(Long orderId, Handler<AsyncResult<Order>> resultHandler);
}
