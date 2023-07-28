FROM eclipse-temurin:17-jre-alpine

# hadolint ignore=DL3018
RUN apk update && \
    apk --no-cache add curl redis sudo && \
    apk upgrade openssl libssl3 libcrypto3 && \
    rm -rf /var/cache/apk/*

WORKDIR /app
COPY set-env-secrets.src entrypoint-wrapper.sh docker-entry*.sh ./

ARG JAR_FILE
ENV JAR_FILENAME=${JAR_FILE}
COPY ${JAR_FILE} fat.jar

RUN adduser --no-create-home --disabled-password tron
# This is required to ensure the folder is writeable
# This folder should correspond with $PERSIST_TRACKING_FOLDER
RUN mkdir -p /persist/tracking && chown -R tron /persist/tracking

RUN mkdir -p /app/build/tomcat && chmod +x ./*.sh  && chown -R tron /app
USER tron
ENTRYPOINT ["/app/entrypoint-wrapper.sh"]

HEALTHCHECK CMD curl --fail http://localhost:8111/actuator/health || exit 1
