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

/** @module store-microservice-js/store_crud_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JStoreCRUDService = Java.type('io.vertx.blueprint.microservice.store.StoreCRUDService');
var Store = Java.type('io.vertx.blueprint.microservice.store.Store');

/**
 @class
*/
var StoreCRUDService = function(j_val) {

  var j_storeCRUDService = j_val;
  var that = this;

  /**

   @public
   @param store {Object} 
   @param resultHandler {function} 
   */
  this.saveStore = function(store, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_storeCRUDService["saveStore(io.vertx.blueprint.microservice.store.Store,io.vertx.core.Handler)"](store != null ? new Store(new JsonObject(Java.asJSONCompatible(store))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param sellerId {string} 
   @param resultHandler {function} 
   */
  this.retrieveStore = function(sellerId, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_storeCRUDService["retrieveStore(java.lang.String,io.vertx.core.Handler)"](sellerId, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param sellerId {string} 
   @param resultHandler {function} 
   */
  this.removeStore = function(sellerId, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_storeCRUDService["removeStore(java.lang.String,io.vertx.core.Handler)"](sellerId, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_storeCRUDService;
};

StoreCRUDService._jclass = utils.getJavaClass("io.vertx.blueprint.microservice.store.StoreCRUDService");
StoreCRUDService._jtype = {
  accept: function(obj) {
    return StoreCRUDService._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(StoreCRUDService.prototype, {});
    StoreCRUDService.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
StoreCRUDService._create = function(jdel) {
  var obj = Object.create(StoreCRUDService.prototype, {});
  StoreCRUDService.apply(obj, arguments);
  return obj;
}
module.exports = StoreCRUDService;