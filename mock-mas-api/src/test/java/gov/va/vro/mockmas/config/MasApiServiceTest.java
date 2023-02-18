package gov.va.vro.mockmas.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
@EnableConfigurationProperties({ MasApiProperties.class, MasOauth2Properties.class })
public class MasApiServiceTest {
  @Autowired
  private MasApiService apiService;

  @Test
  void retrieveRecord350() {
    String annotations = apiService.getAnnotation(350);
    assertNotNull(annotations);
    log.info("annotations: {}", annotations);
  }
}
