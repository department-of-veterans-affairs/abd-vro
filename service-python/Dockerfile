ARG WKHTML_VERSION=3.10.6-0.12.6-small
ARG SERVICE_FOLDER=gradle

# Base - Build Stage: Installs system updates, and adds a user
FROM surnet/alpine-python-wkhtmltopdf:${WKHTML_VERSION} as base

# Used to specify the service folder you want to build if running locally
# No longer write *.pyc files to disk
ENV PYTHONDONTWRITEBYTECODE 1
# Send logs directly to container
ENV PYTHONUNBUFFERED 1

RUN apk update && apk --no-cache add expat=2.5.0-r0 && mkdir -p /home/docker
RUN adduser --disabled-password docker && chown -R 777:777 /home/docker/

# None - Build Stage: Run by Gradle since it doesnt provide a build arg and SERVICE_FOLDER defaults to gradle if not provided
FROM base as layer-gradle
COPY . /home/docker/
WORKDIR /home/docker
RUN pip install --no-cache-dir -r requirements.txt

# ${SERVICE_FOLDER} - Build Stage: Used for local build when selecting a specific service
FROM base as layer-assessclaimdc6510
FROM base as layer-assessclaimdc6522
FROM base as layer-assessclaimdc6602
FROM base as layer-assessclaimdc6602v2
FROM base as layer-assessclaimdc7101
FROM base as layer-assessclaimdc7101v2
FROM base as layer-pdfgenerator
ARG SERVICE_FOLDER
COPY ${SERVICE_FOLDER}/src/. /home/docker/
WORKDIR /home/docker
RUN pip install --no-cache-dir -r requirements.txt

# Final - Build Stage: Add the label and run the consumer
# hadolint ignore=DL3006
FROM layer-${SERVICE_FOLDER} as final
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro
USER docker
CMD ["python", "-u", "main_consumer.py"]
