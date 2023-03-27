FROM eclipse-temurin:17-jre-alpine

# hadolint ignore=DL3018
RUN apk update && apk --no-cache add expat=2.5.0-r0 curl && rm -rf /var/cache/apk/*

RUN adduser --no-create-home --disabled-password docker

HEALTHCHECK CMD curl --fail http://localhost:8081/health || exit 1

WORKDIR /project
ARG ENTRYPOINT_FILE
COPY set-env-secrets.src entrypoint-default.sh ${ENTRYPOINT_FILE}* ./

ARG JAR_FILE
ENV JAR_FILENAME=${JAR_FILE}
COPY ${JAR_FILE} fat.jar

# This is required to ensure the folder is writeable
# This folder should correspond with $PERSIST_TRACKING_FOLDER
RUN mkdir -p /persist/tracking && chown -R docker:docker /persist/tracking

RUN mkdir -p /project/build/tomcat && chmod +x entrypoint.sh  && chown -R docker:docker /project
USER docker
ENTRYPOINT ["/project/entrypoint-default.sh"]
