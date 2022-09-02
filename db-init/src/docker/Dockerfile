FROM flyway/flyway:7.5.4
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro
COPY database /flyway/sql
COPY flyway.conf /flyway/conf
HEALTHCHECK NONE

CMD [ "migrate", "-X" ]
