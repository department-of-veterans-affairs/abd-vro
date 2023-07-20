FROM flyway/flyway:9.21-alpine

# hadolint ignore=DL3018
RUN apk update && apk upgrade && apk --no-cache add libcrypto3 libssl3 openssl  && rm -rf /var/cache/apk/*

COPY database /flyway/sql
COPY flyway.conf /flyway/conf

RUN adduser --no-create-home --disabled-password tron
USER tron
CMD [ "migrate", "-X" ]

HEALTHCHECK NONE
