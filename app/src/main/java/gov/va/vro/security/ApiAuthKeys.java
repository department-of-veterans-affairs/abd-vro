package gov.va.vro.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Configuration
@PropertySource("classpath:conf-apiauthkeys.properties")
@ConfigurationProperties(prefix = "apiauth")
@Validated
@Data
public class ApiAuthKeys {
  private List<String> keys;
}
