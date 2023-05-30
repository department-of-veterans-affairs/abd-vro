FROM flyway/flyway:9.17-alpine

# hadolint ignore=DL3018
RUN apk update && apk --no-cache add libcrypto3=3.0.8-r4 libssl3=3.0.8-r4 openssl=3.0.8-r4  && rm -rf /var/cache/apk/*


COPY database /flyway/sql
COPY flyway.conf /flyway/conf

HEALTHCHECK NONE

RUN adduser --no-create-home --disabled-password tron
USER tron
CMD [ "migrate", "-X" ]
