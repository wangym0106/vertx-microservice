package io.vertx.blueprint.microservice.cart;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@VertxGen(concrete = false)
@ProxyGen
public interface ShoppingCartService {

    String SERVER_NAME = "shopping-cart-eb-service";

    String SERVER_ADDRESS = "service.shopping.cart";

    @Fluent
    ShoppingCartService addCartEvent(CartEvent cartEvent, Handler<AsyncResult<Void>> resultHandler);

    @Fluent

    ShoppingCartService getShoppingCart(String userId, Handler<AsyncResult<ShoppingCart>> resultHandler);

}
