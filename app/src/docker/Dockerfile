FROM eclipse-temurin:17-jre-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

RUN apk update
RUN apk --no-cache add expat=2.4.9-r0 curl=7.83.1-r4

ARG JAR_FILE
ARG ENTRYPOINT_FILE

WORKDIR /project
COPY ${ENTRYPOINT_FILE} entrypoint.sh
RUN mkdir -p /project/build/tomcat && chmod 777 /project/build/tomcat && chmod +x entrypoint.sh
COPY ${JAR_FILE} vro-app.jar
EXPOSE 8081

HEALTHCHECK CMD curl --fail http://localhost:8081/health || exit 1

RUN adduser --no-create-home --disabled-password docker
USER docker
CMD ["sh", "entrypoint.sh"]