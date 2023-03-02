FROM eclipse-temurin:17-jre-alpine
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro
RUN apk update && apk --no-cache add expat=2.5.0-r0 sudo=1.9.12-r1

# The following is needed to run the emerg.sh script as root for access to /persist
COPY emerg.sh /
RUN chmod a+x /emerg.sh && \
  echo 'docker ALL=(ALL) NOPASSWD: /emerg.sh' > /etc/sudoers.d/docker

# TODO?: install redis-cli

ARG JAR_FILE

WORKDIR /project
COPY ${JAR_FILE} vro-console.jar
COPY groovysh.rc .

RUN adduser --disabled-password docker
USER docker
RUN mkdir "$HOME/.groovy" && ln -s /project/groovysh.rc "$HOME/.groovy/"
CMD ["java", "-jar", "vro-console.jar"]
