package io.vertx.blueprint.microservice.shopping.ui;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class ShoppingUIVerticle extends BaseMicroserviceVerticle {

    private Logger logger = LoggerFactory.getLogger(ShoppingUIVerticle.class);

    public void start(Future<Void> future) throws Exception {

        super.start();

        Router router = Router.router(vertx);

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);

        router.route("/eventbus/*").handler(sockJSHandler);

        router.route("/*").handler(StaticHandler.create());

        String host = config().getString("shopping.ui.http.address", "0.0.0.0");

        Integer port = config().getInteger("shopping.ui.http.port", 8080);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port, r -> {
                    if (r.succeeded()) {
                        future.complete();
                        logger.info(String.format("Shopping UI service is running at %d", port));
                    } else {
                        future.fail(r.cause());
                    }

                });


    }
}
