FROM flyway/flyway:7.5.4
COPY database /flyway/sql
COPY flyway.conf /flyway/conf
HEALTHCHECK NONE
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd_vro

CMD [ "migrate", "-X" ]
