package io.vertx.blueprint.microservice.store;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.store.api.RestStoreAPIVerticle;
import io.vertx.blueprint.microservice.store.impl.StoreCRUDServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;


import static io.vertx.blueprint.microservice.store.StoreCRUDService.SERVICE_ADDRESS;
import static io.vertx.blueprint.microservice.store.StoreCRUDService.SERVICE_NAME;

public class StoreVerticle extends BaseMicroserviceVerticle {


    private StoreCRUDService storeCRUDService;

    public void start(Future<Void> future) throws Exception {
        super.start();

        storeCRUDService = new StoreCRUDServiceImpl(vertx, config());

        ProxyHelper.registerService(StoreCRUDService.class, vertx, storeCRUDService, SERVICE_ADDRESS);

        publishEventBusService(SERVICE_NAME,SERVICE_ADDRESS,StoreCRUDService.class)
                .compose(sc -> deployRestVerticle(storeCRUDService))
                .setHandler(future.completer());
    }

    private Future<Void> deployRestVerticle(StoreCRUDService storeCRUDService) {
        Future<String> future = Future.future();
        vertx.deployVerticle(new RestStoreAPIVerticle(storeCRUDService),
                new DeploymentOptions().setConfig(config()),
                future.completer());

        return future.map(r ->null);
    }

}
