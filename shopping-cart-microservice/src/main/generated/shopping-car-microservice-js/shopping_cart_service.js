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

/** @module shopping-car-microservice-js/shopping_cart_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JShoppingCartService = Java.type('io.vertx.blueprint.microservice.cart.ShoppingCartService');
var CartEvent = Java.type('io.vertx.blueprint.microservice.cart.CartEvent');
var ShoppingCart = Java.type('io.vertx.blueprint.microservice.cart.ShoppingCart');

/**
 @class
*/
var ShoppingCartService = function(j_val) {

  var j_shoppingCartService = j_val;
  var that = this;

  /**

   @public
   @param cartEvent {Object} 
   @param resultHandler {function} 
   @return {ShoppingCartService}
   */
  this.addCartEvent = function(cartEvent, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_shoppingCartService["addCartEvent(io.vertx.blueprint.microservice.cart.CartEvent,io.vertx.core.Handler)"](cartEvent != null ? new CartEvent(new JsonObject(Java.asJSONCompatible(cartEvent))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param userId {string} 
   @param resultHandler {function} 
   @return {ShoppingCartService}
   */
  this.getShoppingCart = function(userId, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_shoppingCartService["getShoppingCart(java.lang.String,io.vertx.core.Handler)"](userId, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_shoppingCartService;
};

ShoppingCartService._jclass = utils.getJavaClass("io.vertx.blueprint.microservice.cart.ShoppingCartService");
ShoppingCartService._jtype = {
  accept: function(obj) {
    return ShoppingCartService._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(ShoppingCartService.prototype, {});
    ShoppingCartService.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
ShoppingCartService._create = function(jdel) {
  var obj = Object.create(ShoppingCartService.prototype, {});
  ShoppingCartService.apply(obj, arguments);
  return obj;
}
module.exports = ShoppingCartService;