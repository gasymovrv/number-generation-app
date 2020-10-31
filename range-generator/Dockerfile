FROM openjdk:11-jre-slim
LABEL MAINTAINER="gasymov"

ARG JAVA_OPTS="-Xmx200m"
ARG DEBUG_PORT

ENTRYPOINT java $JAVA_OPTS -jar /opt/range-generator/range-generator.jar

COPY build/libs/range-generator-*.jar /opt/range-generator/range-generator.jar

EXPOSE 8080
EXPOSE $DEBUG_PORT