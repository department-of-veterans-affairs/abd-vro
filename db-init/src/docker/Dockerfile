FROM flyway/flyway:9.17-alpine

# hadolint ignore=DL3018
RUN apk update && rm -rf /var/cache/apk/*


COPY database /flyway/sql
COPY flyway.conf /flyway/conf

HEALTHCHECK NONE

RUN adduser --no-create-home --disabled-password tron
USER tron
CMD [ "migrate", "-X" ]
