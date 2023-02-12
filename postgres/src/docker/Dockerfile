FROM postgres:14.7-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

USER root
COPY init-db.sh /docker-entrypoint-initdb.d/init-db.sh
RUN chmod 777 /docker-entrypoint-initdb.d/init-db.sh
USER postgres
