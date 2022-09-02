# Stage 1 - Build Stage
FROM gradle:7.4.2-jdk17-alpine as build


COPY . .

# Assemble Application
RUN --mount=type=secret,id=all-secrets GITHUB_ACCESS_TOKEN=$(cat /run/secrets/all-secrets | grep GITHUB_ACCESS_TOKEN | awk -F "=" '{print $2}') && \
./gradlew assemble -PGITHUB_ACCESS_TOKEN=$GITHUB_ACCESS_TOKEN

# Stage 2 - Run Stage
FROM openjdk:17-alpine as run
RUN mkdir /app

# Move jars over from build stage
COPY --from=build /home/gradle/app/build/libs/*.jar /app/
WORKDIR /app

# Remove "Plain jar"
RUN rm -f $(grep -L "plain" *)

# Rename jar to consistent name
RUN mv $(ls) app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
