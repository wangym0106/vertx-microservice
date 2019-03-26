#!/usr/bin/env bash

set -e

# 设置 ip
IP=127.0.0.1
unamestr=`uname`
#检查系统
if [[ "$unamestr" != 'Linux' ]]; then

  IP="$(docker-machine ip)"
fi

#暴露ip
export EXTERNAL_IP=$IP

export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#停止 docker
docker-compose -f $DIR/docker-compose.yml stop

docker-compose -f $DIR/docker-compose.yml up -d elasticsearch logstash kibana mysql mongo redis keycloak-server
echo "Waiting for persistence init..."
sleep 30

#开启docker-compose
docker-compose -f $DIR/docker-compose.yml up