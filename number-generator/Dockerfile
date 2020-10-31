FROM openjdk:11-jre-slim
LABEL MAINTAINER="gasymov"

ARG JAVA_OPTS="-Xmx200m"
ARG DEBUG_PORT

ENTRYPOINT java $JAVA_OPTS -jar /opt/number-generator/number-generator.jar

COPY build/libs/number-generator-*.jar /opt/number-generator/number-generator.jar

EXPOSE $DEBUG_PORT