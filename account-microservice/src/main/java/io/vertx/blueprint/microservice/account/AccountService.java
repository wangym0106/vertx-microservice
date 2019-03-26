package io.vertx.blueprint.microservice.account;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

@VertxGen
@ProxyGen
public interface AccountService {

    String SERVICE_NAME = "user-account-web-service";

    String SERVICE_ADDRESS = "service.user.account";


    @Fluent
    AccountService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    AccountService addAccount(Account account, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    AccountService retrieveAccount(String id, Handler<AsyncResult<Account>> resultHandler);

    @Fluent
    AccountService retrieveByUsername(String username, Handler<AsyncResult<Account>> resultHandler);

    @Fluent
    AccountService retrieveAllAccounts(Handler<AsyncResult<List<Account>>> resultHandler);

    @Fluent
    AccountService updateAccount(Account account, Handler<AsyncResult<Account>> resultHandler);

    @Fluent
    AccountService deleteAccount(String id, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    AccountService deleteAllAccounts(Handler<AsyncResult<Void>> resultHandler);
}
