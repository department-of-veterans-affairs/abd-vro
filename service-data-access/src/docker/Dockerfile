FROM eclipse-temurin:19-jre-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

RUN apk update && apk --no-cache add expat=2.4.9-r0

ARG JAR_FILE
ARG ENTRYPOINT_FILE

WORKDIR /project
COPY ${ENTRYPOINT_FILE} entrypoint.sh
RUN chmod +x entrypoint.sh
COPY ${JAR_FILE} service-data-access.jar

RUN adduser --no-create-home --disabled-password docker
USER docker
CMD ["sh", "entrypoint.sh"]
