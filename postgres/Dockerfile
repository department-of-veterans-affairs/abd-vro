FROM postgres:14.11-alpine3.19

# hadolint ignore=DL3018
RUN apk update && \
    apk --no-cache add curl redis sudo && \
    apk upgrade openssl libssl3 libcrypto3 libxml2 && \
    rm -rf /var/cache/apk/*

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
