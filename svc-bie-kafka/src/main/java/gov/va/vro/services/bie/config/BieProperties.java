package gov.va.vro.services.bie.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "bie")
@Setter
@Getter
public class BieProperties {

    private Map<String, String> topicMap;

}
