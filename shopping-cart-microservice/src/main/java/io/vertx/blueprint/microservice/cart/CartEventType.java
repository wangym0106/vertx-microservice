package io.vertx.blueprint.microservice.cart;

import io.vertx.codegen.annotations.VertxGen;

@VertxGen
public enum CartEventType {
    ADD_ITEM,
    REMOVE_ITEM,
    CHECKOUT,
    CLEAR_CART
}
