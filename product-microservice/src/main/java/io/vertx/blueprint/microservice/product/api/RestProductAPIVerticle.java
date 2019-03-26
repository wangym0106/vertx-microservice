package io.vertx.blueprint.microservice.product.api;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.blueprint.microservice.product.Product;
import io.vertx.blueprint.microservice.product.ProductService;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


public class RestProductAPIVerticle extends RestAPIVerticle {
    public static final String SERVICE_NAME = "product-rest-api";

    private static final String API_ADD = "/add";
    private static final String API_RETRIEVE_BY_PAGE = "/products";
    private static final String API_RETRIEVE_ALL = "/products";
    private static final String API_RETRIEVE_PRICE = "/:productId/price";
    private static final String API_RETRIEVE = "/:productId";
    private static final String API_UPDATE = "/:productId";
    private static final String API_DELETE = "/:productId";
    private static final String API_DELETE_ALL = "/all";

    public ProductService productService;

    public RestProductAPIVerticle(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();

        final Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post(API_ADD).handler(this::apiADD);

        router.get(API_RETRIEVE_BY_PAGE).handler(this::apiRetrieveByPage);

        router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);

        router.get(API_RETRIEVE_PRICE).handler(this::apiRetrievePrice);

        router.get(API_RETRIEVE).handler(this::apiRetrieve);

        router.patch(API_UPDATE).handler(this::apiUpdate);

        router.delete(API_DELETE).handler(this::apiDelete);

        router.delete(API_DELETE_ALL).handler(context -> requireLogin(context, this::apiDeleteAll));
    }

    private void apiDeleteAll(RoutingContext context, JsonObject entries) {
        productService.deleteAllProducts(deleteResultHandler(context));
    }

    private void apiDelete(RoutingContext context) {
        @Nullable String product_id = context.request().getParam("product_id");
        productService.deleteProduct(product_id, deleteResultHandler(context));
    }

    private void apiUpdate(RoutingContext context) {
        notImplemented(context);
    }

    private void apiRetrieve(RoutingContext context) {
        @Nullable String product_id = context.request().getParam("product_id");
        productService.retrieveProduct(product_id, resultHandlerNonEmpty(context));
    }

    private void apiRetrievePrice(RoutingContext context) {
        @Nullable String product_id = context.request().getParam("product_id");
        productService.retrieveProductPrice(product_id, resultHandlerNonEmpty(context));
    }

    private void apiRetrieveAll(RoutingContext context) {
        productService.retrieveAllProducts(resultHandler(context, Json::encodePrettily));
    }

    private void apiRetrieveByPage(RoutingContext context) {
        try {
            @Nullable
            String p = context.request().getParam("p");
            int page =p == null ? 1 : Integer.valueOf(p);
            productService.retrieveProductsByPage(page, resultHandler(context, Json::encodePrettily));
        } catch (Exception e) {
            badRequest(context, e);
        }
    }

    private void apiADD(RoutingContext context) {
        try {
            Product product = new Product(new JsonObject(context.getBodyAsString()));
            productService.addProduct(product, resultHandler(context, rs ->{
                String result = new JsonObject().put("message", "product_added").put("product_id", product.getProductId()).encodePrettily();
                context.response().setStatusCode(201)
                    .putHeader("content-type","application/json")
                    .end(result);
            }));
        } catch (DecodeException e) {
            e.fillInStackTrace();
        }

    }
}
