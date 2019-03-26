package io.vertx.blueprint.microservice.order;

import io.vertx.codegen.annotations.VertxGen;

@VertxGen
public enum OrderEventType {
    CREATED,
    PAID,
    SHIPPED,
    DELIVERED
}
