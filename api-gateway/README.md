# API Gateway for VRO

To support serving up APIs implemented in several languages (e.g., Java by the RRD Team and Python used by the CC Team),
this API Gateway acts as a proxy to forward requests to the specified tenant API, as determined by the URI prefix.

The URI prefixes are configured in `application.yml` -- under `spring.cloud.gateway.routes`.

The Swagger UI destinations to tenant APIs are configured in `application.yml` -- under `springdoc.swagger-ui.urls`.

The implementation is based on https://piotrminkowski.com/2020/02/20/microservices-api-documentation-with-springdoc-openapi/,
which uses Spring Boot 3 and Spring Cloud Gateway.
