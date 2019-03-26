/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.blueprint.microservice.account.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import java.util.List;
import io.vertx.core.AsyncResult;
import io.vertx.blueprint.microservice.account.Account;
import io.vertx.core.Handler;


@io.vertx.lang.rxjava.RxGen(io.vertx.blueprint.microservice.account.AccountService.class)
public class AccountService {

  public static final io.vertx.lang.rxjava.TypeArg<AccountService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new AccountService((io.vertx.blueprint.microservice.account.AccountService) obj),
    AccountService::getDelegate
  );

  private final io.vertx.blueprint.microservice.account.AccountService delegate;
  
  public AccountService(io.vertx.blueprint.microservice.account.AccountService delegate) {
    this.delegate = delegate;
  }

  public io.vertx.blueprint.microservice.account.AccountService getDelegate() {
    return delegate;
  }

  public AccountService initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.initializePersistence(resultHandler);
    return this;
  }

  public Single<Void> rxInitializePersistence() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      initializePersistence(fut);
    }));
  }

  public AccountService addAccount(Account account, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.addAccount(account, resultHandler);
    return this;
  }

  public Single<Void> rxAddAccount(Account account) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addAccount(account, fut);
    }));
  }

  public AccountService retrieveAccount(String id, Handler<AsyncResult<Account>> resultHandler) { 
    delegate.retrieveAccount(id, resultHandler);
    return this;
  }

  public Single<Account> rxRetrieveAccount(String id) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveAccount(id, fut);
    }));
  }

  public AccountService retrieveByUsername(String username, Handler<AsyncResult<Account>> resultHandler) { 
    delegate.retrieveByUsername(username, resultHandler);
    return this;
  }

  public Single<Account> rxRetrieveByUsername(String username) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveByUsername(username, fut);
    }));
  }

  public AccountService retrieveAllAccounts(Handler<AsyncResult<List<Account>>> resultHandler) { 
    delegate.retrieveAllAccounts(resultHandler);
    return this;
  }

  public Single<List<Account>> rxRetrieveAllAccounts() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveAllAccounts(fut);
    }));
  }

  public AccountService updateAccount(Account account, Handler<AsyncResult<Account>> resultHandler) { 
    delegate.updateAccount(account, resultHandler);
    return this;
  }

  public Single<Account> rxUpdateAccount(Account account) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      updateAccount(account, fut);
    }));
  }

  public AccountService deleteAccount(String id, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteAccount(id, resultHandler);
    return this;
  }

  public Single<Void> rxDeleteAccount(String id) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      deleteAccount(id, fut);
    }));
  }

  public AccountService deleteAllAccounts(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteAllAccounts(resultHandler);
    return this;
  }

  public Single<Void> rxDeleteAllAccounts() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      deleteAllAccounts(fut);
    }));
  }


  public static AccountService newInstance(io.vertx.blueprint.microservice.account.AccountService arg) {
    return arg != null ? new AccountService(arg) : null;
  }
}
