FROM eclipse-temurin:17-jre-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

ARG JAR_FILE
ARG ENTRYPOINT_FILE

WORKDIR /project
COPY ${ENTRYPOINT_FILE} entrypoint.sh
RUN mkdir -p /project/build/tomcat && chmod 777 /project/build/tomcat && chmod +x entrypoint.sh
COPY ${JAR_FILE} vro-app.jar
EXPOSE 8081

RUN apk --no-cache add curl
HEALTHCHECK CMD curl --fail http://localhost:8081/health || exit 1

RUN adduser --no-create-home --disabled-password docker
USER docker
CMD ["sh", "entrypoint.sh"]
