# Automated Benefit Delivery (ABD) Data Access Service

This service
- Listens RabbitMQ queue where claims are submitted
- Gets health data from Lighthouse Health API for the patient and diagnostic code specied in claim
- Returns health to Camel route

# Technologies

- [Java](https://www.java.com/en/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Gradle](https://gradle.org)
- [Testcontainers](https://www.testcontainers.org/)
- [RabbitMQ](https://www.rabbitmq.com)

# Local Deployment

See the parent project for deployment information.

A `buildgradle.local` is provided if you need to run this independently from the parent project. Replace `build.gradle` with `buildgradle.local` and run the server

```sh
./gradlew bootRun
```
you can also run the tests

```sh
./gradlew test
```
