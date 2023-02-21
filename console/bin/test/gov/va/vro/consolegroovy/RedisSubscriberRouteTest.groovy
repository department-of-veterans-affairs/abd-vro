@RunWith(SpringRunner)
@ContextConfiguration(classes = [RedisConfig, RedisSubscriberRoute])
class RedisSubscriberRouteTest {

  @Autowired
  RedisTemplate<String, Object> redisTemplate

  @Autowired
  CamelContext camelContext

  @Test
  void testRedisSubscriberRoute() throws Exception {
    // Start the Camel route
    camelContext.start()

    // Publish a message to the Redis channel
    redisTemplate.convertAndSend("feature-flag-toggle", "test message")

    // Wait for the message to be processed
    Thread.sleep(1000)

    // Stop the Camel route
    camelContext.stop()
  }
}
