
# TODO: get it working with bitnami/ruby image
# `bundle install` failed with bitnami/ruby:2.7.6

# Or get alpine image working
# FROM ruby:2.7.6-alpine
# RUN apk update && apk --no-cache add expat=2.5.0-r0 git && rm -rf /var/cache/apk/*

FROM ruby:2.7.6

RUN adduser --home /project --disabled-password --gecos "" docker

ARG HEALTHCHECK_PORT_ARG=8080
ENV HEALTHCHECK_PORT=${HEALTHCHECK_PORT_ARG}
# TODO: add healthcheck for Ruby microservice
# HEALTHCHECK CMD curl --fail http://localhost:${HEALTHCHECK_PORT}/actuator/health || exit 1

WORKDIR /project
ARG ENTRYPOINT_FILE
# Workaround to copy ENTRYPOINT_FILE that may not exist
# https://stackoverflow.com/a/46801962
# Copy script that runs entrypoint.sh if it exists
COPY set-env-secrets.src entrypoint-wrapper.sh ${ENTRYPOINT_FILE}* ./

COPY Gemfile Gemfile.lock ./
RUN bundle install

# === Image layers above this line are general and will be reused as cache when building the image.
# Put commands that produce project-specific image layers below this line.

COPY . .
RUN chmod +x entrypoint*.sh && chown -R docker:docker /project

USER docker
ENTRYPOINT ["/project/entrypoint-wrapper.sh"]
