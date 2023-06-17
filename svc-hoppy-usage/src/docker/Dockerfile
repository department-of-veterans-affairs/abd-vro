FROM python:3.9-alpine

# hadolint ignore=DL3018
RUN apk update && apk upgrade && apk --no-cache add git && rm -rf /var/cache/apk/*

RUN adduser --disabled-password --gecos "" tron

WORKDIR /app
ARG ENTRYPOINT_FILE=entrypoint.sh
# Workaround to copy ENTRYPOINT_FILE that may not exist
# https://stackoverflow.com/a/46801962
# Copy script that runs entrypoint.sh if it exists
COPY set-env-secrets.src entrypoint-wrapper.sh ${ENTRYPOINT_FILE}* ./


RUN pip install -v --no-cache-dir git+https://github.com/department-of-veterans-affairs/abd-vro.git@nanotone/lib-hoppy#subdirectory=shared/lib-hoppy

# === Image layers above this line are general and will be reused as cache when building the image.
# Put commands that produce project-specific image layers below this line.

COPY . .
RUN chmod +x entrypoint*.sh && chown -R tron /app

USER tron
ENTRYPOINT ["/app/entrypoint-wrapper.sh"]
