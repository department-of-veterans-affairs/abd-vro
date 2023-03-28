FROM eclipse-temurin:17-jre-alpine

# hadolint ignore=DL3018
RUN apk update && apk --no-cache add expat=2.5.0-r0 curl redis sudo && rm -rf /var/cache/apk/*

RUN adduser --disabled-password docker

# The following is needed to run the emerg.sh script as root for access to /persist
COPY emerg.sh /
RUN chmod a+x /emerg.sh && \
  echo 'docker ALL=(ALL) NOPASSWD: /emerg.sh' > /etc/sudoers.d/docker

WORKDIR /project
ARG ENTRYPOINT_FILE
COPY groovysh.rc set-env-secrets.src entrypoint-wrapper.sh ${ENTRYPOINT_FILE}* ./

ARG JAR_FILE
COPY ${JAR_FILE} fat.jar

USER docker
RUN mkdir "$HOME/.groovy" && ln -s /project/groovysh.rc "$HOME/.groovy/"
ENTRYPOINT ["/project/entrypoint-wrapper.sh"]
