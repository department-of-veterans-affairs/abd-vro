package gov.va.vro.abd_data_access;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.abd_data_access.model.AbdEvidence;
import gov.va.vro.abd_data_access.service.FhirClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
class FhirClientTest {
  @Autowired private FhirClient client;

  @Value("classpath:expected-json/lh-patient01-7101.json")
  private Resource expectedResource;

  @Test
  public void testLighthouseAPIAccess() throws Exception {
    AbdEvidence evidence = client.getMedicalEvidence(CommonData.claim01);

    assertNotNull(evidence);
    if (evidence.getBloodPressures() != null) {
      assertTrue(evidence.getBloodPressures().size() > 0);
      int measuredBPCount =
          evidence.getBloodPressures().stream()
              .filter(s -> s.getDiastolic() != null | s.getSystolic() != null)
              .collect(Collectors.toList())
              .size();
      assertEquals(measuredBPCount, evidence.getBloodPressures().size());
    }
    ;

    if (evidence.getMedications() != null) {
      assertTrue(evidence.getMedications().size() > 0);
    }
    ;

    if (evidence.getConditions() != null) {
      assertTrue(evidence.getConditions().size() > 0);
    }

    if (evidence.getProcedures() != null) {
      assertTrue(evidence.getProcedures().size() > 0);
    }
  }
}
