FROM eclipse-temurin:17-jre-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

RUN apk update && apk --no-cache add expat=2.5.0-r0

ARG JAR_FILE
ARG ENTRYPOINT_FILE

WORKDIR /project
COPY build/docker/entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh
COPY build/docker/service-data-access*.jar service-data-access.jar

RUN adduser --no-create-home --disabled-password docker
USER docker
CMD ["sh", "entrypoint.sh"]
