package io.vertx.blueprint.microservice.store.impl;

import io.vertx.blueprint.microservice.store.Store;
import io.vertx.blueprint.microservice.store.StoreCRUDService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class StoreCRUDServiceImpl implements StoreCRUDService {


    private static final String COLLECTION = "store";

    private final MongoClient mongoClient;

    public StoreCRUDServiceImpl(Vertx vertx, JsonObject config) {
        this.mongoClient = MongoClient.createNonShared(vertx, config);
    }

    @Override
    public void saveStore(Store store, Handler<AsyncResult<Void>> resultHandler) {
        mongoClient.save(COLLECTION,
                new JsonObject()
                        .put("_id", store.getSellerId())
                        .put("name", store.getName())
                        .put("description", store.getDescription())
                        .put("opentime", store.getOpenTime()),
                ar-> {
                      if (ar.succeeded()) {
                          resultHandler.handle(Future.succeededFuture());
                      } else {
                          resultHandler.handle(Future.failedFuture(ar.cause()));
                      }
                }
        );

    }

    @Override
    public void retrieveStore(String sellerId, Handler<AsyncResult<Store>> resultHandler) {

        JsonObject jsonObject = new JsonObject().put("_id",sellerId);
        mongoClient.findOne(COLLECTION, jsonObject, null, ar ->{

            if (ar.succeeded()) {
                if (ar.result() ==null) {
                    resultHandler.handle(Future.succeededFuture());
                } else {
                    Store store = new Store(ar.result().put("_id",ar.result().getString("_id")));
                    resultHandler.handle(Future.succeededFuture(store));
                }
            } else {
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void removeStore(String sellerId, Handler<AsyncResult<Void>> resultHandler) {

        JsonObject _id = new JsonObject().put("_id",sellerId);
        mongoClient.removeDocument(COLLECTION, _id, ar->{
            if (ar.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }
}
