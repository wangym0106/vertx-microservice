package io.vertx.blueprint.microservice.cart.impl;

import io.vertx.blueprint.microservice.cache.CounterService;
import io.vertx.blueprint.microservice.cart.*;
import io.vertx.blueprint.microservice.common.functional.Functional;
import io.vertx.blueprint.microservice.order.Order;
import io.vertx.blueprint.microservice.product.ProductTuple;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.List;
import java.util.stream.Collectors;

public class CheckoutServiceImpl implements CheckoutService {


    private final  Vertx vertx;

    private final ServiceDiscovery discovery;
    public CheckoutServiceImpl(Vertx vertx, ServiceDiscovery discovery) {
        this.vertx = vertx;

        this.discovery = discovery;
    }

    @Override
    public void checkout(String userId, Handler<AsyncResult<CheckoutResult>> handler) {

        if (userId == null) {
            handler.handle(Future.failedFuture(new IllegalArgumentException("参数信息错误")));
            return;
        }

        Future<ShoppingCart> cartFuture = getCurrentCart(userId);

        Future<CheckoutResult> orderFuture = cartFuture.compose(cart -> checkAvailableInventory(cart)
                .compose(checkResult -> {
                    if (checkResult.getBoolean("es")) {
                        double totalPrice = calculateTotalPrice(cart);
                        Order order = new Order().setBuyerId(userId)
                                .setPayId("TEST")
                                .setProducts(cart.getProductItems())
                                .setTotalPrice(totalPrice);

                        return retrieveCounter("order")
                                .compose(id -> sendOrderAwaitResult(order.setOrderId(id)))
                                .compose(result -> saveCheckoutEvent(userId).map(v -> result));
                    } else {
                        //库存计算失败

                        return Future.succeededFuture(new CheckoutResult().setMessage(checkResult.getString("message")));
                    }

                }));
        orderFuture.setHandler(handler);

    }

    /**
     * 保存检查事件信息
     * @param
     * @return
     */
    private Future<Void> saveCheckoutEvent(String userId) {
        Future<ShoppingCartService> future = Future.future();
        EventBusService.getProxy(discovery, ShoppingCartService.class, future.completer());
        return future.compose(service -> {
            Future<Void> resultFuture = Future.future();
            CartEvent checkoutEvent = CartEvent.createCheckoutEvent(userId);
            service.addCartEvent(checkoutEvent,resultFuture.completer());
            return resultFuture;
        });

    }


    private Future<CheckoutResult> sendOrderAwaitResult(Order order) {
        Future<CheckoutResult> checkoutResultFuture = Future.future();
        vertx.eventBus().send(CheckoutService.ORDER_EVENT_ADDRESS, order.toJson(),ar -> {
            if (ar.succeeded()) {
                checkoutResultFuture.complete(new CheckoutResult((JsonObject)ar.result().body()));
            } else {
                checkoutResultFuture.fail(ar.cause());
            }
        });
        return checkoutResultFuture;

    }

    private Future<Long> retrieveCounter(String key) {

        Future<Long> f = Future.future();
        EventBusService.getProxy(discovery, CounterService.class,ar ->{
            if (ar.succeeded()) {
                CounterService result = ar.result();
                result.addThenRetrieve(key, f.completer());
            } else {
                f.fail(f.cause());
            }
        });
        return f;
    }

    private double calculateTotalPrice(ShoppingCart cart) {
        return cart.getProductItems().stream().map(p -> p.getAmount() * p.getPrice()).reduce(0.0d, (a,b) -> a+b);
    }

    private Future<JsonObject> checkAvailableInventory(ShoppingCart cart) {
        Future<List<JsonObject>> listFuture = getInventoryEndPoint().compose(client -> {
            List<Future<JsonObject>> collect = cart.getProductItems().stream().map(product -> getInventory(product, client))
                    .collect(Collectors.toList());
            return Functional.allOfFutures(collect).map(r -> {
                ServiceDiscovery.releaseServiceObject(discovery, client);
                return r;
            });
        });

        return listFuture.map(inventories -> {
            JsonObject result = new JsonObject();
            List<JsonObject> insufficient = inventories.stream().filter(item -> item.getInteger("inventory") - item.getInteger("amount") < 0)
                    .collect(Collectors.toList());
            if (insufficient.size() >0 ) {
                String insufficientList = insufficient.stream().map(item -> item.getString("id"))
                        .collect(Collectors.joining(","));

                result.put("message", String.format("Insufficient inventory available for product %s",insufficientList))
                    .put("res",false);

            } else {
                result.put("res",true);
            }


            return result;
        });
    }

    private Future<JsonObject> getInventory(ProductTuple product, HttpClient client) {

        Future<Integer> future = Future.future();

        client.get("/"+ product.getProductId(), response -> {
            if (response.statusCode()== 200 ) {
                response.bodyHandler(buffer -> {
                    try {
                        Integer inventory = Integer.valueOf(buffer.toString());
                        future.complete(inventory);
                    } catch (NumberFormatException ex) {
                        future.fail(ex);
                    }
                });
            } else {
                future.fail("not found " + product.getProductId());
            }
        })
                .exceptionHandler(future::fail)
                .end();
        return future.map(inv -> new JsonObject()
                .put("id", product.getProductId())
                .put("inventory", inv)
                .put("amount", product.getAmount())
        );
    }

    private Future<HttpClient> getInventoryEndPoint() {
        Future<HttpClient> future = Future.future();
        HttpEndpoint.getClient(discovery, new JsonObject().put("name", "inventory-rest-api"),future.completer());
        return future;
    }

    private Future<ShoppingCart> getCurrentCart(String userId) {
        Future<ShoppingCartService> future = Future.future();
        EventBusService.getProxy(discovery,ShoppingCartService.class,future.completer());

        return future.compose(service -> {
           Future<ShoppingCart> shoppingCartFuture = Future.future();
           service.getShoppingCart(userId, shoppingCartFuture.completer());
           return shoppingCartFuture.compose(c -> {
              if (c.isEmpty() || c ==null) {
                    return Future.failedFuture(new IllegalStateException("Invalid shopping cart"));
              } else {
                  return  Future.succeededFuture(c);
              }
           });
        });
    }
}
