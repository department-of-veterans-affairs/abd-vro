FROM flyway/flyway:9.15-alpine

RUN apk update && apk --no-cache add expat=2.5.0-r0 && rm -rf /var/cache/apk/*

COPY database /flyway/sql
COPY flyway.conf /flyway/conf

HEALTHCHECK NONE

RUN adduser --no-create-home --disabled-password docker
USER docker
CMD [ "migrate", "-X" ]
