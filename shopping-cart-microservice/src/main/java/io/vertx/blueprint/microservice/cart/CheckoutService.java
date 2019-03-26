package io.vertx.blueprint.microservice.cart;

import io.vertx.blueprint.microservice.cart.impl.CheckoutServiceImpl;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.ServiceDiscovery;


@VertxGen
@ProxyGen
public interface CheckoutService {

    String SERVER_NAME = "shopping-checkout-eb-service";

    String SERVER_ADDRESS = "service.shopping.cart.checkout";

    String PAYMENT_EVENT_ADDRESS = "events.service.shopping.to.payment";

    String ORDER_EVENT_ADDRESS = "events.service.shopping.to.order";

    static CheckoutService createCheckoutService(Vertx vertx, ServiceDiscovery discovery) {
      return   new CheckoutServiceImpl(vertx, discovery);
    }
    void checkout(String userId, Handler<AsyncResult<CheckoutResult>> handler);
}
