package io.vertx.blueprint.microservice.product;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface ProductService {


    String SERVICE_NAME = "product-eb-service";

    String SERVICE_ADDRESS = "service.product";

    @Fluent
    ProductService initializePersistence(Handler<AsyncResult<Void>> resultHandler);


    @Fluent
    ProductService addProduct(Product product, Handler<AsyncResult<Void>> resultHandler);


    @Fluent
    ProductService retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler);


    @Fluent
    ProductService retrieveProductPrice(String productId, Handler<AsyncResult<JsonObject>> resultHandler);


    @Fluent
    ProductService retrieveAllProducts(Handler<AsyncResult<List<Product>>> resultHandler);


    @Fluent
    ProductService retrieveProductsByPage(int page, Handler<AsyncResult<List<Product>>> resultHandler);


    @Fluent
    ProductService deleteProduct(String productId, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    ProductService deleteAllProducts(Handler<AsyncResult<Void>> resultHandler);

}
