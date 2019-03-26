FROM java:8-jre

ENV VERTICLE_HOME target/cache-infrastructure-fat.jar

ENV VERTICLE_HOME /opt/verticles

COPY VERTICLE_HOME $VERTICLE_HOME

COPY src/config/docker.json $VERTICLE_HOME

WORKDIR $VERTICLE_HOME

ENTRYPOINT ["sh", "-C"]

CMD [""java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -jar cache-infrastructure-fat.jar -cluster -conf docker.json"]

