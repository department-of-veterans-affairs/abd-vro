FROM openjdk:17
ARG JAR_FILE
ARG ENTRYPOINT_FILE
WORKDIR /project
COPY ${ENTRYPOINT_FILE} entrypoint.sh
RUN chmod +x entrypoint.sh
COPY ${JAR_FILE} service-data-access.jar
CMD ["sh", "entrypoint.sh"]
