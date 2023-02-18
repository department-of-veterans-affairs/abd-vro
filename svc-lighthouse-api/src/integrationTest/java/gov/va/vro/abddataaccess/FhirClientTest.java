package gov.va.vro.abddataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.abddataaccess.service.FhirClient;
import gov.va.vro.model.AbdEvidence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integrationTest")
class FhirClientTest {
  @Autowired private FhirClient client;

  @Test
  public void testLighthouseApiAccess() throws Exception {
    AbdEvidence evidence = client.getMedicalEvidence(CommonData.claim01);

    assertNotNull(evidence);
    if (evidence.getBloodPressures() != null) {
      assertTrue(evidence.getBloodPressures().size() > 0);
      long measuredBpCount =
          evidence.getBloodPressures().stream()
              .filter(s -> s.getDiastolic() != null | s.getSystolic() != null)
              .count();
      assertEquals(measuredBpCount, evidence.getBloodPressures().size());
    }

    if (evidence.getMedications() != null) {
      assertTrue(evidence.getMedications().size() > 0);
    }

    if (evidence.getConditions() != null) {
      assertTrue(evidence.getConditions().size() > 0);
    }

    if (evidence.getProcedures() != null) {
      assertTrue(evidence.getProcedures().size() > 0);
    }
  }
}
