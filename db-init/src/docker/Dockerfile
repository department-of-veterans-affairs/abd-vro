FROM flyway/flyway:9.16-alpine

RUN apk update && apk --no-cache add expat=2.5.0-r0 libcrypto3=3.0.8-r3 libssl3=3.0.8-r3 openssl=3.0.8-r3 && rm -rf /var/cache/apk/*

COPY database /flyway/sql
COPY flyway.conf /flyway/conf

HEALTHCHECK NONE

RUN adduser --no-create-home --disabled-password tron
USER tron
CMD [ "migrate", "-X" ]
