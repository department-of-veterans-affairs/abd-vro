FROM eclipse-temurin:17-jre-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

# hadolint ignore=DL3018
RUN apk update && apk --no-cache add expat=2.5.0-r0 curl

ARG JAR_FILE

WORKDIR /project
COPY ${JAR_FILE} mock-bip-ce-api.jar

HEALTHCHECK CMD curl --fail http://localhost:8095/actuator/health || exit 1

RUN adduser --no-create-home --disabled-password docker
USER docker
CMD ["java", "-jar", "mock-bip-ce-api.jar"]
