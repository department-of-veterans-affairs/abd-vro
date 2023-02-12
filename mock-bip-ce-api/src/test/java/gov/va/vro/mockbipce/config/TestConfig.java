package gov.va.vro.mockbipce.config;

import gov.va.vro.mockshared.JwtGenerator;
import gov.va.vro.mockshared.JwtSpecification;
import gov.va.vro.mockshared.KeystoreSpec;
import gov.va.vro.mockshared.RestUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {
  @Value("${http.client.ssl.trust-store-password}")
  private String trustStorePassword;

  @Value("${http.client.ssl.key-store-password}")
  private String keyStorePassword;

  @Value("${truststore}")
  private String trustStoreBase64;

  @Value("${keystore}")
  private String keyStoreBase64;

  @Autowired private JwtProps jwtProps;

  /**
   * Gets the https rest template.
   *
   * @param builder Rest template builder
   * @return RestTemplate
   */
  @SneakyThrows
  @Bean(name = "httpsRestTemplate")
  public RestTemplate getHttpsRestTemplate(RestTemplateBuilder builder) {
    KeystoreSpec spec =
        new KeystoreSpec(keyStoreBase64, keyStorePassword, trustStoreBase64, trustStorePassword);
    return RestUtil.getHttpsRestTemplate(builder, spec);
  }

  @Bean
  public JwtSpecification getJwtSpecification() {
    return new JwtSpecification(jwtProps);
  }

  @Bean
  public JwtGenerator getJwtGenerator() {
    JwtSpecification spec = getJwtSpecification();
    return new JwtGenerator(spec);
  }
}
