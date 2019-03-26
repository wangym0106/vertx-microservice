package io.vertx.blueprint.microservice.store;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@VertxGen
@ProxyGen
public interface StoreCRUDService {

    String SERVICE_NAME = "store-eb-service";

    String SERVICE_ADDRESS = "service.store";

    void saveStore(Store store, Handler<AsyncResult<Void>> resultHandler);

    void retrieveStore(String sellerId, Handler<AsyncResult<Store>> resultHandler);

    void removeStore(String sellerId, Handler<AsyncResult<Void>> resultHandler);


}
