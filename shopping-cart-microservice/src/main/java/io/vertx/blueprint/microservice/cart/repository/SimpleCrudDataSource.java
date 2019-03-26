package io.vertx.blueprint.microservice.cart.repository;

import rx.Single;

import java.util.Optional;

public interface SimpleCrudDataSource<T,ID> {

    Single<Void> save(T entity);

    Single<Optional<T>> selectOne(ID id);

    Single<Void> deleteOne(ID id);
}
