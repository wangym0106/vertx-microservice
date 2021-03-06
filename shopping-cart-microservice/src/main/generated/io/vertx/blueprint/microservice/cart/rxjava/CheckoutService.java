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

package io.vertx.blueprint.microservice.cart.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.servicediscovery.ServiceDiscovery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.blueprint.microservice.cart.CheckoutResult;


@io.vertx.lang.rxjava.RxGen(io.vertx.blueprint.microservice.cart.CheckoutService.class)
public class CheckoutService {

  public static final io.vertx.lang.rxjava.TypeArg<CheckoutService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new CheckoutService((io.vertx.blueprint.microservice.cart.CheckoutService) obj),
    CheckoutService::getDelegate
  );

  private final io.vertx.blueprint.microservice.cart.CheckoutService delegate;
  
  public CheckoutService(io.vertx.blueprint.microservice.cart.CheckoutService delegate) {
    this.delegate = delegate;
  }

  public io.vertx.blueprint.microservice.cart.CheckoutService getDelegate() {
    return delegate;
  }

  public static CheckoutService createCheckoutService(Vertx vertx, ServiceDiscovery discovery) { 
    CheckoutService ret = CheckoutService.newInstance(io.vertx.blueprint.microservice.cart.CheckoutService.createCheckoutService(vertx.getDelegate(), discovery.getDelegate()));
    return ret;
  }

  public void checkout(String userId, Handler<AsyncResult<CheckoutResult>> handler) { 
    delegate.checkout(userId, handler);
  }

  public Single<CheckoutResult> rxCheckout(String userId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      checkout(userId, fut);
    }));
  }


  public static CheckoutService newInstance(io.vertx.blueprint.microservice.cart.CheckoutService arg) {
    return arg != null ? new CheckoutService(arg) : null;
  }
}
