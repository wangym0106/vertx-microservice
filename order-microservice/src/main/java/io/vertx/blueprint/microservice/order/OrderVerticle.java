package io.vertx.blueprint.microservice.order;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.order.impl.OrderServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

import static io.vertx.blueprint.microservice.order.OrderService.SERVICE_ADDRESS;
import static io.vertx.blueprint.microservice.order.OrderService.SERVICE_NAME;

public class OrderVerticle extends BaseMicroserviceVerticle {

    private OrderService orderService;

    public void start(Future<Void> future) throws Exception {

        super.start();

        this.orderService = new OrderServiceImpl(vertx, config());

        ProxyHelper.registerService(OrderService.class, vertx, orderService, SERVICE_ADDRESS);

        initOrderDatabase()
                .compose(databaseOk -> publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, OrderService.class))
                .compose(servicePublished -> prepareDispatcher())
                .compose(dispatcherPrepared -> deployRestVerticle())
                .setHandler(future.completer());
    }

    private Future<Void> deployRestVerticle() {
        Future<String> future = Future.future();
        vertx.deployVerticle(new RawOrderDispatcher(orderService),
                new DeploymentOptions().setConfig(config()),
                future.completer()
        );
        return future.map(r -> null);

    }

    private Future<Void> prepareDispatcher() {
        Future<String> future = Future.future();
        vertx.deployVerticle(new RawOrderDispatcher(orderService),
                    new DeploymentOptions().setConfig(config()),
                    future.completer()

                );
        return future.map(r ->null);
    }


    private Future<Void> initOrderDatabase() {
        Future<Void> future = Future.future();
        orderService.initializePersistence(future.completer());
        return future;

    }
}
