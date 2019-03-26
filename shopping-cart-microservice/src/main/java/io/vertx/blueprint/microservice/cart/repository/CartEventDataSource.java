package io.vertx.blueprint.microservice.cart.repository;

import io.vertx.blueprint.microservice.cart.CartEvent;
import rx.Observable;


public interface CartEventDataSource extends SimpleCrudDataSource<CartEvent, Long> {

    Observable<CartEvent> streamByUser(String id);
}
