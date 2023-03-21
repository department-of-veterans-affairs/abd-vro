FROM eclipse-temurin:17-jre-alpine

# hadolint ignore=DL3018
RUN apk update && apk --no-cache add expat=2.5.0-r0 curl && rm -rf /var/cache/apk/*

ARG JAR_FILE
ARG ENTRYPOINT_FILE

WORKDIR /project
COPY ${ENTRYPOINT_FILE} entrypoint.sh
COPY ${JAR_FILE} vro-app.jar
RUN mkdir -p /project/build/tomcat && chmod 777 /project/build/tomcat && chmod +x entrypoint.sh
EXPOSE 8081

HEALTHCHECK CMD curl --fail http://localhost:8081/health || exit 1

RUN adduser --no-create-home --disabled-password docker

# This is required to ensure the folder is writeable
# This folder should correspond with $PERSIST_TRACKING_FOLDER
RUN mkdir -p /persist/tracking && chown docker:docker /persist/tracking

USER docker
CMD ["sh", "entrypoint.sh"]
