FROM openjdk:17-bullseye

RUN mkdir -p /app
WORKDIR /app

COPY app/ ./

ENTRYPOINT [ "./gradlew", "bootrun" ]