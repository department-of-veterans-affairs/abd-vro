FROM flyway/flyway:9.5-alpine

LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

COPY database /flyway/sql
COPY flyway.conf /flyway/conf

HEALTHCHECK NONE

RUN adduser --no-create-home --disabled-password docker
USER docker
CMD [ "migrate", "-X" ]
