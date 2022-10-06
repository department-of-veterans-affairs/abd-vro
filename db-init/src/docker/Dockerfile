FROM flyway/flyway:9.4.0-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro
COPY database /flyway/sql
COPY flyway.conf /flyway/conf
HEALTHCHECK NONE

RUN useradd --no-create-home docker
USER docker

CMD [ "migrate", "-X" ]
