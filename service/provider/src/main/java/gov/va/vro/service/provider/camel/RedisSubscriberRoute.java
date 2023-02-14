package gov.va.vro.service.provider.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisSubscriberRoute extends RouteBuilder {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void configure() throws Exception {
        from("redis:subscribe?template=redisTemplate&Channel=feature-flag-toggle")
            .process(exchange -> {
                String message = exchange.getIn().getBody(String.class);
                System.out.println("Received message: " + message);
            });
    }
    
}
