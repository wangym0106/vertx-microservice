package io.vertx.blueprint.microservice.cart.repository.impl;


import io.vertx.blueprint.microservice.cart.CartEvent;
import io.vertx.blueprint.microservice.cart.CartEventType;
import io.vertx.blueprint.microservice.cart.repository.CartEventDataSource;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import rx.Observable;
import rx.Single;

import java.util.Optional;


public class CartEventDataSourceImpl implements CartEventDataSource {

    private final JDBCClient jdbcClient;

    public CartEventDataSourceImpl(Vertx vertx, JsonObject config) {
        this.jdbcClient = JDBCClient.createNonShared(io.vertx.rxjava.core.Vertx.newInstance(vertx),config);

        this.jdbcClient.rxGetConnection().flatMap(conn ->
                conn.rxExecute(INIT_STATEMENT)
                .doAfterTerminate(conn::close)
                ).subscribe();
    }
    @Override
    public Observable<CartEvent> streamByUser(String id) {
        JsonArray params = new JsonArray().add(id).add(id);
        return jdbcClient.rxGetConnection().flatMapObservable(conn ->

                    conn.rxQueryWithParams(INIT_STATEMENT,params)
                            .map(ResultSet::getRows)
                            .flatMapObservable(Observable::from)
                            .map(this::wrapCartEvent)
                            .doAfterTerminate(conn::close)
                );
    }

    private CartEvent wrapCartEvent(JsonObject raw) {
        return new CartEvent()
                .setUserId(raw.getString("user_id"))
                .setProductId(raw.getString("product_id"))
                .setCreatedAt(raw.getLong("create_at"))
                .setCartEventType(CartEventType.valueOf(raw.getString("type")));
    }

    @Override
    public Single<Void> save(CartEvent cartEvent) {
        JsonArray params = new JsonArray().add(cartEvent.getCartEventType().name())
                .add(cartEvent.getUserId())
                .add(cartEvent.getProductId())
                .add(cartEvent.getAmount())
                .add(cartEvent.getCreatedAt() > 0 ? cartEvent.getCreatedAt() : System.currentTimeMillis());
        return jdbcClient.rxGetConnection().flatMap(conn -> conn.rxUpdateWithParams(INIT_STATEMENT,params).map(v -> (Void)null).doAfterTerminate(conn::close));
    }

    @Override
    public Single<Optional<CartEvent>> selectOne(Long id) {

        return jdbcClient.rxGetConnection().flatMap(conn -> conn.rxQueryWithParams(STREAM_STATEMENT, new JsonArray().add(id)).map(ResultSet::getRows).map(list -> {
            if (list.isEmpty()) {
                return Optional.<CartEvent>empty();
            } else {
                return Optional.of(list.get(0))
                        .map(this::wrapCartEvent);
            }
        }).doAfterTerminate(conn::close));
    }

    @Override
    public Single<Void> deleteOne(Long id) {
        return Single.error(new RuntimeException("Delete is not allowed"));
    }

    // SQL Statement

    private static final String INIT_STATEMENT = "CREATE TABLE IF NOT EXISTS `cart_event` (\n" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
            "  `type` VARCHAR(20) NOT NULL,\n" +
            "  `user_id` varchar(45) NOT NULL,\n" +
            "  `product_id` varchar(45) NOT NULL,\n" +
            "  `amount` int(11) NOT NULL,\n" +
            "  `created_at` bigint(20) NOT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `INDEX_USER` (`user_id`) )";

    private static final String SAVE_STATEMENT = "INSERT INTO `cart_event` (`type`, `user_id`, `product_id`, `amount`, `created_at`) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String RETRIEVE_STATEMENT = "SELECT * FROM `cart_event` WHERE id = ?";

    private static final String STREAM_STATEMENT = "SELECT * FROM cart_event c\n" +
            "WHERE c.user_id = ? AND c.created_at > coalesce(\n" +
            "    (SELECT created_at FROM cart_event\n" +
            "\t WHERE user_id = ? AND (`type` = \"CHECKOUT\" OR `type` = \"CLEAR_CART\")\n" +
            "     ORDER BY cart_event.created_at DESC\n" +
            "     LIMIT 1\n" +
            "     ), 0)\n" +
            "ORDER BY c.created_at ASC;";
}
