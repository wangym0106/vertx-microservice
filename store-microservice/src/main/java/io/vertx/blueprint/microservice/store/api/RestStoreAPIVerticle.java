package io.vertx.blueprint.microservice.store.api;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.blueprint.microservice.store.Store;
import io.vertx.blueprint.microservice.store.StoreCRUDService;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


public class RestStoreAPIVerticle extends RestAPIVerticle {

    private static final String SERVICE_NAME = "shop-rest-api";

    private static final String API_SAVE = "/save";
    private static final String API_RETRIEVE = "/:sellerId";
    private static final String API_CLOSE = "/:sellerId";

    private final StoreCRUDService storeCRUDService;

    public RestStoreAPIVerticle(StoreCRUDService storeCRUDService) {
        this.storeCRUDService = storeCRUDService;
    }

    public void start(Future<Void> future) throws Exception {
        super.start();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post(API_SAVE).handler(this::apiSave);
        router.get(API_RETRIEVE).handler(this::apiRetrieve);
        router.delete(API_CLOSE).handler(this::apiClose);

        String address = config().getString("store.http.address", "0.0.0.0");
        Integer port = config().getInteger("store.port", 8085);

        createHttpServer(router, address, port)
                .compose(serverCreated -> publishEventBusService(SERVICE_NAME, address, StoreCRUDService.class))
                .setHandler(future.completer());
    }

    private void apiClose(RoutingContext context) {

        @Nullable String sellerId = context.request().getParam("sellerId");
        storeCRUDService.removeStore(sellerId, deleteResultHandler(context));
    }

    private void apiRetrieve(RoutingContext context) {
        @Nullable String sellerId = context.request().getParam("sellerId");
        storeCRUDService.retrieveStore(sellerId, resultHandlerNonEmpty(context));

    }

    private void apiSave(RoutingContext context) {
        Store store = new Store(new JsonObject(context.getBodyAsString()));
        if (store.getSellerId() == null) {
            badRequest(context, new IllegalArgumentException("参数错误"));
        } else {
            JsonObject jsonObject = new JsonObject()
                    .put("message", "store_saved")
                    .put("sellerId", store.getSellerId());
            storeCRUDService.saveStore(store, resultVoidHandler(context,jsonObject));
        }
    }
}
