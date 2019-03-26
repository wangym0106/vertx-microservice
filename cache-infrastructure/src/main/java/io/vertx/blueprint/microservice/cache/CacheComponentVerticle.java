package io.vertx.blueprint.microservice.cache;

import io.vertx.blueprint.microservice.cache.CounterService;
import io.vertx.blueprint.microservice.cache.impl.DefaultCounterServiceImpl;
import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;


public class CacheComponentVerticle extends BaseMicroserviceVerticle {


    @Override
    public void start(Future<Void> future) throws Exception {

        super.start();

        CounterService counterService = new DefaultCounterServiceImpl(vertx, config());

        ProxyHelper.registerService(CounterService.class, vertx, counterService, CounterService.SERVICE_ADDRESS);

        publishEventBusService(CounterService.SERVICE_NAME, CounterService.SERVICE_ADDRESS, CounterService.class)
                .setHandler(future.completer());
    }
}
