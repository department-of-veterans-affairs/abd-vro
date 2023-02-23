package gov.va.vro.service.provider.services;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;

@Service
public class RedisFeatureFlagService {
    
    private final RedisPubSubReactiveCommands<String, String> redisPubSubReactiveCommands;

    public RedisFeatureFlagService(RedisPubSubReactiveCommands<String, String> redisPubSubReactiveCommands) {
        this.redisPubSubReactiveCommands = redisPubSubReactiveCommands;
    }

    @PostConstruct
    public void setupSubscriber() {
        redisPubSubReactiveCommands.subscribe("feature-flag-toggle").subscribe();
        redisPubSubReactiveCommands.observeChannels().doOnNext(channelMessage -> {
            if ("feature-flag-toggle".equals(channelMessage.getChannel())){

            }
        }).subscribe();
    }
}
