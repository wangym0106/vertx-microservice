package io.vertx.blueprint.microservice.common;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.JDBCDataSource;
import io.vertx.servicediscovery.types.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public abstract class BaseMicroserviceVerticle extends AbstractVerticle {

    private static final String LOG_EVENT_ADDRESS = "events.log";

    private static final Logger logger = LoggerFactory.getLogger(BaseMicroserviceVerticle.class);

    protected ServiceDiscovery discovery ;

    protected CircuitBreaker circuitBreaker;

    protected Set<Record> registeredRecords = new ConcurrentHashSet<>();


    /**
     * 启动设置参数化
     * @throws Exception
     */
    @Override
    public void start() throws Exception{

        discovery = ServiceDiscovery.create(vertx,new ServiceDiscoveryOptions().setBackendConfiguration(config()));

        JsonObject cbOptions = config().getJsonObject("circuit-breaker") !=null ? config().getJsonObject("circuit-breaker") : new JsonObject();

        circuitBreaker = CircuitBreaker.create(cbOptions.getString("name","circuit-breaker"), vertx,
                new CircuitBreakerOptions()
                        .setMaxFailures(cbOptions.getInteger("max-failures",5))
                        .setTimeout(cbOptions.getLong("time-out",10000L))
                        .setFallbackOnFailure(true)
                        .setResetTimeout(cbOptions.getLong("reset-timeout", 30000L))

        );
    }

    /**
     * 加载api配置信息
     * @param name
     * @param host
     * @param port
     * @return
     */
    protected Future<Void> publishHttpEndpoint(String name ,String host, int port){
        Record record = HttpEndpoint.createRecord(name, host, port ,"/" ,
                    new JsonObject().put("api.name",config().getString("api.name"))
                );

        return publish(record);
    }

    /**
     * 加载网关配置信息
     * @param host
     * @param port
     * @return
     */
    protected Future<Void> publishApiGateway(String host, int port){
        Record record = HttpEndpoint.createRecord("api-gateway",true,host, port ,"/",null)
                                    .setType("api-gateway");
        return publish(record);
    }

    /**
     * 加载消息对象
     * @param name
     * @param address
     * @return
     */
    protected Future<Void> publishMessageSource(String name, String address){
        Record record = MessageSource.createRecord(name, address);
        return publish(record);
    }

    /**
     *  加载jdbc
     * @param name
     * @param location
     * @return
     */
    protected Future<Void> publishJDBCDataSource(String name, JsonObject location){
        Record record = JDBCDataSource.createRecord(name, location, new JsonObject());
        return publish(record);
    }

    /**
     * 加载事件
     * @param name
     * @param address
     * @param classService
     * @return
     */
    protected Future<Void> publishEventBusService(String name, String address, Class classService){
        Record record = EventBusService.createRecord(name, address, classService);
        return publish(record);
    }

    /**
     * 信息发布
     * @param record
     * @return
     */
    protected  Future<Void> publish(Record record){

        if (discovery ==null) {
            try {
                start();
            }catch (Exception e){
                throw new IllegalStateException("Cannot create discovery service");
            }
        }

        Future<Void> future = Future.future();

        discovery.publish(record ,ar -> {
            if (ar.succeeded()) {
                    registeredRecords.add(record);
                    logger.info("Service <" + ar.result().getName() + "> published");
                    future.complete();
            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    /**
     * 日志事件记录器
     * @param type
     * @param data
     */
    protected void publishLogEvent(String type, JsonObject data) {
        JsonObject msg = new JsonObject().put("type",type)
                                         .put("message",data);
        vertx.eventBus().publish(LOG_EVENT_ADDRESS,msg);

    }

    protected void publishLogEvent(String type, JsonObject data, boolean succeed){
        JsonObject msg = new JsonObject().put("type",type)
                                        .put("status",succeed )
                                        .put("message",data);
        vertx.eventBus().publish(LOG_EVENT_ADDRESS,msg);
    }

    /**
     * 容器关闭
     * @param future
     * @throws Exception
     */
    @Override
    public void stop(Future<Void> future) throws Exception{
        List<Future> futures = new ArrayList<>();

        registeredRecords.forEach(r -> {
            Future cleanupFuture =Future.future();

            futures.add(cleanupFuture);

            discovery.unpublish(r.getRegistration(),cleanupFuture.completer());
        });

        if (futures.isEmpty()){
            discovery.close();
            future.complete();
        } else {
            CompositeFuture.all(futures)
                    .setHandler(ar -> {
                        discovery.close();
                        if (ar.failed()) {
                            future.fail(ar.cause());
                        } else {
                            future.complete();
                        }
                    });

        }


    }

}
