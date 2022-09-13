FROM openjdk:17
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro
ARG JAR_FILE
ARG ENTRYPOINT_FILE
WORKDIR /project
COPY ${ENTRYPOINT_FILE} entrypoint.sh
RUN chmod +x entrypoint.sh
COPY ${JAR_FILE} service-data-access.jar
RUN adduser --home /home/docker docker
USER docker
CMD ["sh", "entrypoint.sh"]
