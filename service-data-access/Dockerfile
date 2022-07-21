FROM openjdk:17-bullseye

WORKDIR /app

COPY gradle/wrapper/ gradle/wrapper/
COPY gradlew ./
COPY build.gradle ./

COPY src ./src

ENTRYPOINT [ "./gradlew", "bootrun" ]