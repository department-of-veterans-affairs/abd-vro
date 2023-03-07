FROM eclipse-temurin:17-jre-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro
RUN apk update && apk --no-cache add expat=2.5.0-r0

ARG JAR_FILE

WORKDIR /project
COPY ${JAR_FILE} xample-workflows.jar

RUN adduser --disabled-password docker
USER docker
CMD ["java", "-jar", "xample-workflows.jar"]
