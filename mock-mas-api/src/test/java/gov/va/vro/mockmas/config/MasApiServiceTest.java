package gov.va.vro.mockmas.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.model.mas.MasCollectionAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
@EnableConfigurationProperties({MasApiProperties.class, MasOauth2Properties.class})
public class MasApiServiceTest {
  @Autowired private MasApiService apiService;

  @Autowired private MasOauth2Properties oauth2Properties;

  @Test
  void retrieveAnnotation350() {
    if (oauth2Properties.getClientSecret() == null) {
      return; // ignore when client secret is not available (scripts/setenv.sh has not been run)
    }
    List<MasCollectionAnnotation> annotations = apiService.getAnnotation(350);
    // Sanity check if this is the record from the dev server
    assertNotNull(annotations);
    assertEquals(2, annotations.size());
    assertEquals(34, annotations.get(0).getDocuments().size());
  }

  @Test
  void orderExam350() {
    if (oauth2Properties.getClientSecret() == null) {
      return; // ignore when client secret is not available (scripts/setenv.sh has not been run)
    }
    // OrderExamResponse response = apiService.orderExam(350);
  }
}
