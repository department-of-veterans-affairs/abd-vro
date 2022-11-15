package gov.va.vro.service.provider.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.redis.RedisConstants;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.support.builder.ValueBuilder;
import org.springframework.stereotype.Component;

@Component
class RedisRoutes extends RouteBuilder {

  // https://camel.apache.org/manual/faq/how-do-i-configure-endpoints.html#HowdoIconfigureendpoints-ReferringbeansfromEndpointURIs
  // `bean:redisTemplate` is provided by RedisConnectionConfig.redisTemplate()
  static final String REDIS_ENDPOINT = "spring-redis://?redisTemplate=#bean:redisTemplate";

  @Override
  public void configure() throws Exception {
    saveIncomingClaimToRedis();
    savePdfRequestToRedis();
  }

  void saveIncomingClaimToRedis() throws Exception {
    String tapBasename = PrimaryRoutes.INCOMING_CLAIM_WIRETAP;
    RouteDefinition routeDef = from(wiretapEndpoint(tapBasename)).routeId("redis-" + tapBasename);
    appendRedisCommand(routeDef, "HSET", redisKey("claimSubmissionId"), "submitted-claim")
        .to(REDIS_ENDPOINT);
  }

  void savePdfRequestToRedis() throws Exception {
    String tapBasename = PrimaryRoutes.GENERATE_PDF_WIRETAP;
    RouteDefinition routeDef = from(wiretapEndpoint(tapBasename)).routeId("redis-" + tapBasename);
    appendRedisCommand(routeDef, "HSET", redisKey("claimSubmissionId"), "submitted-pdf")
        .to(REDIS_ENDPOINT);
  }

  private String wiretapEndpoint(String tapBasename) {
    return "rabbitmq:tap-" + tapBasename + "?exchangeType=topic&queue=redis-" + tapBasename;
  }

  private ValueBuilder redisKey(String idField) {
    return constant("tracking-").append(jsonpath("." + idField));
  }

  private RouteDefinition appendRedisCommand(
      RouteDefinition routeDef, String command, ValueBuilder redisKey, String hashKey) {
    return routeDef
        .setHeader(RedisConstants.COMMAND, constant(command))
        // the Redis key where hash is stored
        .setHeader(RedisConstants.KEY, redisKey)
        // hash key
        .setHeader(RedisConstants.FIELD, constant(hashKey))
        // hash value as a String b/c redisTemplate's defaultSerializer is StringRedisSerializer
        .setHeader(RedisConstants.VALUE, body().convertToString());
  }
}
