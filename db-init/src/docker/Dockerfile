FROM flyway/flyway:7.5.4
COPY db-init/src/main/resources/database /flyway/sql
COPY db-init/src/main/resources/flyway.conf /flyway/conf
HEALTHCHECK NONE
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

CMD [ "migrate", "-X" ]
