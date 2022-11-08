FROM eclipse-temurin:17-jre-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

RUN apk update && apk --no-cache add expat=2.5.0-r0 curl=7.83.1-r4

ARG JAR_FILE
ARG ENTRYPOINT_FILE

WORKDIR /project
COPY app/build/docker/entrypoint.sh entrypoint.sh
RUN mkdir -p /project/build/tomcat && chmod 777 /project/build/tomcat && chmod +x entrypoint.sh
COPY app/build/docker/app*.jar vro-app.jar
EXPOSE 8081

HEALTHCHECK CMD curl --fail http://localhost:8081/health || exit 1

RUN adduser --no-create-home --disabled-password docker
USER docker
CMD ["sh", "entrypoint.sh"]
