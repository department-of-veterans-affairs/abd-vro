FROM flyway/flyway:9.16-alpine

LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

RUN apk update && apk --no-cache add expat=2.5.0-r0

COPY database /flyway/sql
COPY flyway.conf /flyway/conf

HEALTHCHECK NONE

RUN adduser --no-create-home --disabled-password docker
USER docker
CMD [ "migrate", "-X" ]
