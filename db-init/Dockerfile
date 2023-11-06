FROM flyway/flyway:9.22-alpine

# hadolint ignore=DL3018
RUN apk update && apk upgrade && apk --no-cache add libcrypto3 libssl3 openssl  && rm -rf /var/cache/apk/* 
RUN pip3 install --no-cache-dir sqlfluff==2.1.2
COPY database /flyway/sql
COPY flyway.conf /flyway/conf

RUN adduser --no-create-home --disabled-password tron
USER tron
CMD [ "migrate", "-X" ]

HEALTHCHECK NONE
