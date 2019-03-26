package io.vertx.blueprint.microservice.order.api;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.blueprint.microservice.order.OrderService;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Optional;

public class RestOrderAPIVerticle extends RestAPIVerticle {
    private static final String SERVICE_NAME = "order-rest-api";

    private static final String API_RETRIEVE = "/orders/:orderId";
    private static final String API_RETRIEVE_FOR_ACCOUNT = "/user/:id/orders";

    private final OrderService service;

    public RestOrderAPIVerticle(OrderService service) {
        this.service = service;
    }

    public void start(Future<Void> future) throws Exception {
        super.start();

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.get(API_RETRIEVE).handler(this::apiRetrieve);

        router.get(API_RETRIEVE_FOR_ACCOUNT).handler(context -> requireLogin(context, this::apiRetrieveForAccount));

        String host = config().getString("order.http.host", "0.0.0.0");

        Integer port = config().getInteger("order.http.port", 8090);

        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
                .setHandler(future.completer());
    }

    private void apiRetrieveForAccount(RoutingContext context, JsonObject principal) {
        @Nullable String userId = context.request().getParam("userId");
        String authId = Optional.ofNullable(principal.getString("userId")).orElse(TEST_USER);
        if (authId.equals(userId)) {
            service.retrieveOrdersForAccount(userId, resultHandler(context, Json::encodePrettily));
        } else {
            context.fail(400);
        }

    }

    private void apiRetrieve(RoutingContext context) {
        try {
            @Nullable Long orderId = Long.parseLong(context.request().getParam("orderId"));
            service.retrieveOrder(orderId, resultHandlerNonEmpty(context));
        } catch (Exception ex) {
            notFound(context);
            ex.printStackTrace();
        }

    }

    private static final String TEST_USER = "TEST666";

}
