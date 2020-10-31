FROM openjdk:11-jre-slim
LABEL MAINTAINER="gasymov"

ARG JAVA_OPTS="-Xmx200m"
ARG DEBUG_PORT

ENTRYPOINT java $JAVA_OPTS -jar /opt/number-api/number-api.jar

COPY build/libs/number-api-*.jar /opt/number-api/number-api.jar

EXPOSE 8080
EXPOSE $DEBUG_PORT