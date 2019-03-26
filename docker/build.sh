#! /usr/bin/env bash

set -e

DIR=  "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

docker build -t "vertx-microservice/api-gateway" $DIR/../api-gateway
docker build -t "vertx-microservice/cache-infrastructure" $DIR/../cache-infrastructure
docker build -t "vertx-microservice/inventory-microservice" $DIR/../inventory-microservice
docker build -t "vertx-microservice/monitor-dashboard" $DIR/../monitor-dashboard
docker build -t "vertx-microservice/order-microservice" $DIR/../order-microservice
docker build -t "vertx-microservice/product-microservice" $DIR/../product-microservice
docker build -t "vertx-microservice/shopping-cart-microservice" $DIR/../shopping-cart-microservice
docker build -t "vertx-microservice/store-microservice" $DIR/../store-microservice
docker build -t "vertx-microservice/account-microservice" $DIR/../account-microservice