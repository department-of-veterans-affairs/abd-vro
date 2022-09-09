FROM openjdk:17
ARG JAR_FILE
ARG ENTRYPOINT_FILE
WORKDIR /project
COPY ${ENTRYPOINT_FILE} entrypoint.sh
RUN mkdir -p /project/build/tomcat && chmod 777 /project/build/tomcat && chmod +x entrypoint.sh
COPY ${JAR_FILE} starter-java.jar
EXPOSE 8081
HEALTHCHECK CMD curl --fail http://localhost:8081/health || exit 1
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

RUN useradd --no-create-home someuser
USER someuser
CMD ["sh", "entrypoint.sh"]
