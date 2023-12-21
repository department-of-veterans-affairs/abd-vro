FROM flyway/flyway:10.4-alpine

# hadolint ignore=DL3018
RUN <<EOR
apk update
apk upgrade
apk --no-cache add libcrypto3 libssl3 openssl postgresql-client
rm -rf /var/cache/apk/*
pip3 install --no-cache-dir sqlfluff==2.1.2
pip3 install --no-cache-dir jinja2==3.1.3
EOR

COPY database /flyway/sql
COPY flyway.conf /flyway/conf

RUN adduser --no-create-home --disabled-password tron
USER tron
CMD [ "migrate", "-X" ]

HEALTHCHECK NONE
