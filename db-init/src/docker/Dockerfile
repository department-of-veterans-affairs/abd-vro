FROM flyway/flyway:7.5.4
COPY main/resources/database /flyway/sql
COPY main/resources/flyway.conf /flyway/conf
HEALTHCHECK NONE
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

CMD [ "migrate", "-X" ]
