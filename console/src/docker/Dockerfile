FROM eclipse-temurin:17-jre-alpine
RUN apk update && apk --no-cache add expat=2.4.9-r0

ARG JAR_FILE

WORKDIR /project
COPY ${JAR_FILE} vro-console.jar
COPY groovysh.rc .

RUN adduser --disabled-password docker
USER docker
RUN mkdir "$HOME/.groovy" && ln -s /project/groovysh.rc "$HOME/.groovy/"
CMD ["java", "-jar", "vro-console.jar"]
