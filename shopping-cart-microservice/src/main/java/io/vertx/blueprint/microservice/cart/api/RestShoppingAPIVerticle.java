package io.vertx.blueprint.microservice.cart.api;

import io.vertx.blueprint.microservice.cart.CartEvent;
import io.vertx.blueprint.microservice.cart.CheckoutService;
import io.vertx.blueprint.microservice.cart.ShoppingCartService;
import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Optional;


public class RestShoppingAPIVerticle extends RestAPIVerticle {


    private static final String SERVER_NAME = "shopping-cart-rest-api";

    private ShoppingCartService shoppingCartService;

    private CheckoutService checkoutService;

    private static final String API_CHECKOUT = "/checkout";
    private static final String API_ADD_CART_EVENT = "/events";
    private static final String API_GET_CART = "/cart";

    private static final String TEST_USER = "TEST666";



    public RestShoppingAPIVerticle(ShoppingCartService shoppingCartService, CheckoutService checkoutService) {
        this.shoppingCartService = shoppingCartService;
        this.checkoutService = checkoutService;
    }

    public void start(Future<Void> future) throws  Exception {
        super.start();

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post(API_CHECKOUT).handler(context -> requireLogin(context, this::apiCheckout));
        router.post(API_ADD_CART_EVENT).handler(context -> requireLogin(context, this::apiAddCartEvent));
        router.get(API_GET_CART).handler(context -> requireLogin(context, this::apiGetCart));

        enableLocalSession(router);

        String host = config().getString("shopping.cart.http.address", "0.0.0.0");

        Integer port = config().getInteger("shopping.cart.http.port", 8084);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVER_NAME, host, port))
                .setHandler(future.completer());
    }

    private void apiGetCart(RoutingContext context, JsonObject params) {
        String userid = Optional.ofNullable(params.getString("userId")).orElse(TEST_USER);
        shoppingCartService.getShoppingCart(userid,resultHandler(context));
    }

    private void apiAddCartEvent(RoutingContext context, JsonObject params) {
        String userid = Optional.ofNullable(params.getString("userId")).orElse(TEST_USER);
        CartEvent cartEvent = new CartEvent(context.getBodyAsJson());
        if (validateEvent(userid, cartEvent)) {
            shoppingCartService.addCartEvent(cartEvent, resultVoidHandler(context,201));
        } else {
            context.fail(401);
        }

    }

    private boolean validateEvent(String userid, CartEvent cartEvent) {
        return cartEvent.getUserId() != null && cartEvent.getAmount() != null && cartEvent.getAmount() > 0 &&
                cartEvent.getUserId().equals(userid);
    }

    private void apiCheckout(RoutingContext context, JsonObject params) {
        String userid = Optional.ofNullable(params.getString("userId")).orElse(TEST_USER);
        checkoutService.checkout(userid, resultHandler(context));

    }


}
