FROM postgres:14.7-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro
RUN apk update && apk --no-cache add sudo=1.9.12_p2-r1

USER root

# The following is needed to run the init-folder.sh script as root (to run chown)
# before the postgres container does it's normal thing
COPY our-entrypoint.sh init-folder.sh /
RUN chmod a+x /our-entrypoint.sh /init-folder.sh && \
  # https://ostechnix.com/add-delete-and-grant-sudo-privileges-to-users-in-alpine-linux/
  # https://docs.alpinelinux.org/user-handbook/0.1a/Working/post-install.html
  echo 'postgres ALL=(ALL) NOPASSWD: /init-folder.sh' > /etc/sudoers.d/postgres

# To initialize the DB with our Flyway user
COPY init-db.sh /docker-entrypoint-initdb.d/init-db.sh
RUN chmod 777 /docker-entrypoint-initdb.d/init-db.sh

# SecRel requires that containers not run as root
USER postgres
ENTRYPOINT ["/our-entrypoint.sh"]
