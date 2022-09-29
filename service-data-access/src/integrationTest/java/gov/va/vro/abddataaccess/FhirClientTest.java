package gov.va.vro.abddataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.abddataaccess.model.AbdEvidence;
import gov.va.vro.abddataaccess.service.FhirClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
class FhirClientTest {
  @Autowired private FhirClient client;

  @Test
  public void testLighthouseApiAccess() throws Exception {
    AbdEvidence evidence = client.getMedicalEvidence(CommonData.claim01);

    assertNotNull(evidence);
    if (evidence.getBloodPressures() != null) {
      assertTrue(evidence.getBloodPressures().size() > 0);
      int measuredBpCount =
          evidence.getBloodPressures().stream()
              .filter(s -> s.getDiastolic() != null | s.getSystolic() != null)
              .collect(Collectors.toList())
              .size();
      assertEquals(measuredBpCount, evidence.getBloodPressures().size());
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
