package io.vertx.blueprint.microservice.order;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.product.ProductTuple;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.MessageSource;

import java.util.List;
import java.util.stream.Collectors;

public class RawOrderDispatcher  extends BaseMicroserviceVerticle {

    private final OrderService orderService;

    public RawOrderDispatcher(OrderService orderService) {
        this.orderService = orderService;
    }

    public void start(Future<Void> future) throws Exception {
        super.start();
        MessageSource.<JsonObject>getConsumer(discovery,
                new JsonObject()
                    .put("name", "shopping-order-message-source"),
                ar ->{
                    if (ar.succeeded()) {
                        MessageConsumer<JsonObject> result = ar.result();
                        result.handler(message -> {
                            Order order = wrapRawOrder(message.body());
                            dispatchOrder(order, message);
                        });
                        future.complete();
                    } else {
                        future.fail(future.cause());
                    }

                }
        );
    }

    private void dispatchOrder(Order order, Message<JsonObject> message) {
        Future<Void> orderCreateFuture = Future.future();
        orderService.createOrder(order,orderCreateFuture.completer());

        orderCreateFuture
                .compose(orderCreated -> applyInventoryChanges(order))
                .setHandler(ar-> {
                    if (ar.succeeded()) {
                        CheckoutResult checkoutResult = new CheckoutResult("checkout_success", order);
                        message.reply(checkoutResult.toJson());
                        publishLogEvent("checkout", checkoutResult.toJson(),true);
                    } else {
                        message.fail(500, ar.cause().getMessage());
                        ar.cause().printStackTrace();
                    }
                });
    }

    private Future<Void> applyInventoryChanges(Order order) {
        Future<Void> future = Future.future();


        Future<HttpClient> clientFuture = Future.future();

        HttpEndpoint.getClient(discovery,
                new JsonObject().put("name", "inventory-rest-api"),
                clientFuture.completer()
                 );
        return clientFuture.compose(client -> {
            List<Future> futureList = order.getProducts()
                    .stream()
                    .map(item -> {
                        Future<Void> resultFuture = Future.future();
                        String url = String.format("/%s/decrease?n=%d", item.getProductId(), item.getAmount());
                        client.put(url, response -> {
                            if (response.statusCode() == 200) {
                                resultFuture.completer();
                            } else {
                                resultFuture.fail(response.statusMessage());
                            }
                        }).exceptionHandler(resultFuture::fail)
                                .end();
                        return resultFuture;
                    }).collect(Collectors.toList());
            CompositeFuture.all(futureList).setHandler(ar -> {
                    if (ar.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(future.cause());
                    }
                ServiceDiscovery.releaseServiceObject(discovery, client);
            });
            return  future;
        });
    }

    private Order wrapRawOrder(JsonObject body) {
        return new Order(body).setCreateTime(System.currentTimeMillis());
    }

}
