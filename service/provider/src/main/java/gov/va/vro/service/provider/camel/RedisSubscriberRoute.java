package gov.va.vro.service.provider.camel;

import javax.annotation.PostConstruct;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import gov.va.vro.service.provider.services.RedisFeatureFlagService;

@Component
public class RedisSubscriberRoute extends RouteBuilder {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisFeatureFlagService featureFlagService;

    @PostConstruct
    public void init() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        container.addMessageListener(new MessageListenerAdapter(handleMessage), new ChannelTopic("feature-flag-toggle"));
    }

    private void handleMessage(String message) {
        create
    }

    @Override
    public void configure() throws Exception {
        from("redis:subscribe?template=redisTemplate&Channel=feature-flag-toggle")
            .process(exchange -> {
                String message = exchange.getIn().getBody(String.class);
                System.out.println("Received message: " + message);write 
            });
    }
    
}
