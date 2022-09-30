package gov.va.vro.config.propmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
public class OpenApi {
  @NestedConfigurationProperty private final Info info = new Info();
}
