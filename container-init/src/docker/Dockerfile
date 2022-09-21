FROM alpine:3.7
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro
RUN apk --no-cache add postgresql-client=10.10-r0
COPY init_pg.sql /usr/local/share
CMD [ "sleep 10 && echo \"attempting to create new db with username:$POSTGRES_USER\" && psql \"postgres://$POSTGRES_ROOT_USER:$POSTGRES_ROOT_PASSWORD@$POSTGRES_HOST:5432/postgres\" -v username=\"$POSTGRES_USER\" -v dbname=\"$POSTGRES_DB\" -v password=\"$POSTGRES_PASSWORD\" -f /usr/local/share/init_pg.sql" ]
