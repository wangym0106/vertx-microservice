package io.vertx.blueprint.microservice.account;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;


public class UserAccountVerticle extends BaseMicroserviceVerticle {

    private AccountService accountService;

    @Override
    public void start(Future<Void> future) throws Exception {

        super.start();


    }
}
