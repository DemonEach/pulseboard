FROM gradle:8.11.1-jdk23 AS java-build

WORKDIR /app

ARG LOGS_PATH
ENV LOGS_PATH $LOGS_PATH

COPY --chown=gradle:gradle build.gradle settings.gradle /app/
COPY --chown=gradle:gradle src /app/src
RUN mkdir -p LOGS_PATH

RUN gradle assemble --no-daemon

ARG SERVER_PORT
ENV SERVER_PORT $SERVER_PORT

FROM openjdk:23
COPY --from=java-build /app/build/libs/pulseboard-0.0.1-SNAPSHOT.jar pulseboard.jar
CMD ["java", "-jar", "pulseboard.jar"]
