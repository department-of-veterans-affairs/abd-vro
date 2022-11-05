package gov.va.vro.service.provider.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.redis.RedisConstants;
import org.springframework.stereotype.Component;

@Component
class RedisRoutes extends RouteBuilder {

  // https://camel.apache.org/manual/faq/how-do-i-configure-endpoints.html#HowdoIconfigureendpoints-ReferringbeansfromEndpointURIs
  // `bean:redisTemplate` is provided by RedisConfig.redisTemplate()
  static final String REDIS_ENDPOINT = "spring-redis://?redisTemplate=#bean:redisTemplate";

  @Override
  public void configure() throws Exception {
    final String tapBasename = PrimaryRoutes.INCOMING_CLAIM_WIRETAP;
    from("rabbitmq:tap-" + tapBasename + "?exchangeType=topic&queue=redis-" + tapBasename)
        .routeId("redis-save-" + tapBasename)
        //        .convertBodyTo(String.class)
        // set property so it can be used to set the RedisConstants.KEY header
        .setProperty("claimSubmissionId", jsonpath(".claimSubmissionId"))
        // Configure to populate claim in Redis hash
        .setHeader(RedisConstants.COMMAND, constant("HSET"))
        .setHeader(RedisConstants.KEY, simple("tracking-${exchangeProperty[claimSubmissionId]}"))
        .setHeader(RedisConstants.FIELD, constant("request"))
        .setHeader(RedisConstants.VALUE, body().convertToString())
        .to(REDIS_ENDPOINT)
        // TODO: remove debugging
        .to("log:" + tapBasename + "?plain=true");
  }
  /* Console commands for testing:
    redis.keys "*"
    wireTap generate-pdf
    redis.hget "tracking-1234", "claim"
    redis.del "tracking-1234"
  */
}
