package io.vertx.blueprint.microservice.cart.impl;

import io.vertx.blueprint.microservice.cart.CartEvent;
import io.vertx.blueprint.microservice.cart.ShoppingCart;
import io.vertx.blueprint.microservice.cart.ShoppingCartService;
import io.vertx.blueprint.microservice.cart.repository.CartEventDataSource;
import io.vertx.blueprint.microservice.cart.repository.impl.CartEventDataSourceImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;

public class ShoppingCartServiceImpl implements ShoppingCartService {


    private final CartEventDataSource repository;

    private final ServiceDiscovery discovery;


    public ShoppingCartServiceImpl(Vertx vertx, ServiceDiscovery discovery, JsonObject config) {
        this.discovery = discovery;
        this.repository = new CartEventDataSourceImpl(vertx, config);
    }

    @Override
    public ShoppingCartService addCartEvent(CartEvent cartEvent, Handler<AsyncResult<Void>> resultHandler) {
        return null;
    }

    @Override
    public ShoppingCartService getShoppingCart(String userId, Handler<AsyncResult<ShoppingCart>> resultHandler) {
        return null;
    }
}
