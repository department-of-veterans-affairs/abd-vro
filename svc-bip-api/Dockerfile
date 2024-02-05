FROM eclipse-temurin:17-jre-alpine

# curl is needed for HEALTHCHECK, python3 and py3-pip are added for Python support
# hadolint ignore=DL3018
RUN apk update && \
    apk --no-cache add curl redis sudo python3 py3-pip && \
    apk upgrade openssl libssl3 libcrypto3 && \
    rm -rf /var/cache/apk/*

WORKDIR /app

# Create the test directory
RUN mkdir test

# Copy the requirements.txt file and the Python script into the /app/test directory
COPY src/test/python/requirements.txt test/
COPY src/test/python/env-integration-test.py test/

# Install Python dependencies
RUN pip3 install --no-cache-dir -r test/requirements.txt

# Copy script that runs docker-entrypoint.sh if it exists, otherwise run jar file
COPY set-env-secrets.src entrypoint-wrapper.sh docker-entry*.sh ./

ARG JAR_FILE
ENV JAR_FILENAME=${JAR_FILE}
COPY ${JAR_FILE} fat.jar

RUN adduser --no-create-home --disabled-password tron
RUN chmod +x ./*.sh && chown -R tron /app
USER tron

# The exec form of the Dockerfile ENTRYPOINT is used so that there is no shell wrapping the Java process.
# The advantage is that the java process responds to KILL signals sent to the container.
ENTRYPOINT ["/app/entrypoint-wrapper.sh"]

# 8080 is the default port that spring-actuator uses
ARG HEALTHCHECK_PORT_ARG=8080
ENV HEALTHCHECK_PORT=${HEALTHCHECK_PORT_ARG}
HEALTHCHECK CMD curl --fail http://localhost:${HEALTHCHECK_PORT}/actuator/health || exit 1