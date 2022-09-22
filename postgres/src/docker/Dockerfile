FROM postgres:14.5
USER root
COPY init-db.sh /docker-entrypoint-initdb.d/init-db.sh
RUN chmod 777 /docker-entrypoint-initdb.d/init-db.sh
USER postgres

