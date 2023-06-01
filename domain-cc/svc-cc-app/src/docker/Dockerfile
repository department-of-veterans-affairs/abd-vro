# This file is referenced from local.python.container-service-convention.
FROM python:3.10.0-alpine

# hadolint ignore=DL3018
RUN apk update && apk --no-cache add libcrypto1.1 libcrypto3 libssl1.1 libssl3 && rm -rf /var/cache/apk/*

RUN adduser --disabled-password tron

HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=5 CMD [ "CMD-SHELL", "pgrep python || exit 1" ]

WORKDIR /domain-cc/svc-cc-app
ARG ENTRYPOINT_FILE=entrypoint.sh
# Workaround to copy ENTRYPOINT_FILE that may not exist
# https://stackoverflow.com/a/46801962
# Copy script that runs entrypoint.sh if it exists
COPY set-env-secrets.src entrypoint-wrapper.sh ${ENTRYPOINT_FILE}* ./

# Used to specify the service folder you want to build if running locally
# No longer write *.pyc files to disk
ENV PYTHONDONTWRITEBYTECODE 1
# Send logs directly to container
ENV PYTHONUNBUFFERED 1

# === Image layers above this line are general and will be reused as cache when building the image.
# Put commands that produce project-specific image layers below this line.

# Copy over all files specified in local.python.container-service-convention
COPY . ./

RUN pip install --no-cache-dir -r requirements.txt

RUN chmod +x entrypoint*.sh && chown -R tron .
USER tron
# The exec form of the Dockerfile ENTRYPOINT is used so that there is no shell wrapping the Java process.
# The advantage is that the java process responds to KILL signals sent to the container.
#ENTRYPOINT ["/domainc-cc/docker/rabbitmq/entrypoint.sh"]
ENTRYPOINT ["/domain-cc/svc-cc-app/src/docker/entrypoint.sh"]
