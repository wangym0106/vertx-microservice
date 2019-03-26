package io.vertx.blueprint.microservice.cart;

import io.vertx.blueprint.microservice.cart.api.RestShoppingAPIVerticle;
import io.vertx.blueprint.microservice.cart.impl.CheckoutServiceImpl;
import io.vertx.blueprint.microservice.cart.impl.ShoppingCartServiceImpl;
import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;


public class CartVerticle extends BaseMicroserviceVerticle {

    private ShoppingCartService shoppingCartService;

    private CheckoutService checkoutService;

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();

        this.shoppingCartService = new ShoppingCartServiceImpl(vertx,discovery, config());

        this.checkoutService = new CheckoutServiceImpl(vertx, discovery);

        ProxyHelper.registerService(ShoppingCartService.class, vertx,shoppingCartService, ShoppingCartService.SERVER_ADDRESS);

        ProxyHelper.registerService(CheckoutService.class, vertx, checkoutService, CheckoutService.SERVER_ADDRESS);

        publishEventBusService(CheckoutService.SERVER_NAME, CheckoutService.SERVER_ADDRESS,CheckoutService.class)
                .compose(service ->
                        publishEventBusService(ShoppingCartService.SERVER_NAME,ShoppingCartService.SERVER_ADDRESS,ShoppingCartService.class)
                        )
                .compose(service ->
                        publishMessageSource("shopping-payment-message-source",CheckoutServiceImpl.PAYMENT_EVENT_ADDRESS)
                        )
                .compose(service ->
                        publishMessageSource("shopping-order-message-source",CheckoutServiceImpl.ORDER_EVENT_ADDRESS)
                        )
                .compose(sourcePublish -> deployRestVerticle())
                .setHandler(future.completer());

    }

    private Future<Void> deployRestVerticle() {
        Future<String> future = Future.future();
        vertx.deployVerticle( new RestShoppingAPIVerticle(shoppingCartService, checkoutService),
                new DeploymentOptions().setConfig(config()),
                future.completer()
                );
        return  future.map(v -> (Void)null);
    }
}
