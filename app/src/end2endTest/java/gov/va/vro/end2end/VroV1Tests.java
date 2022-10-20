package gov.va.vro.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import gov.va.vro.end2end.util.PdfText;
import gov.va.vro.end2end.util.RestHelper;
import gov.va.vro.end2end.util.TestSetup;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class VroV1Tests {
  private void testAssessment(TestSetup setup, RestHelper helper) throws Exception {
    String actual = helper.getAssessment(setup);
    String expected = setup.getAssessment();
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  private void testPdfGeneration(TestSetup setup, RestHelper helper) throws Exception {
    String actual = helper.generatePdf(setup);
    String expected = setup.getGeneratePdfResponse();
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  private void testGeneratedPdf(TestSetup setup, RestHelper helper) throws Exception {
    byte[] pdf = helper.getPdf(setup);
    PdfText pdfText = PdfText.getInstance(pdf);

    JsonNode bpReadings = setup.getBpReadingsNode();
    if (bpReadings != null) {
      assertTrue(bpReadings.isArray());
      assertEquals(bpReadings.size(), pdfText.countBpReadings());
    } else {
      assertEquals(0, pdfText.countBpReadings());
    }

    JsonNode meds = setup.getMedicationsNode();
    assertTrue(meds.isArray());
    assertEquals(meds.size(), pdfText.countMedications());

    JsonNode vetInfo = setup.getVeteranInfoNode();
    assertTrue(pdfText.hasVeteranInfo(vetInfo));
  }

  private void testEnd2End(TestSetup setup, RestHelper helper) throws Exception {
    testAssessment(setup, helper);
    testPdfGeneration(setup, helper);
    testGeneratedPdf(setup, helper);
  }

  @Test
  public void positive7101Case01() throws Exception {
    TestSetup setup = TestSetup.getInstance("test-7101-01");

    RestHelper helper = new RestHelper();
    helper.setApiKey("test-key-01");

    testEnd2End(setup, helper);
  }

  @Test
  public void positive6602Case01() throws Exception {
    TestSetup setup = TestSetup.getInstance("test-6602-01");

    RestHelper helper = new RestHelper();
    helper.setApiKey("test-key-01");

    testEnd2End(setup, helper);
  }
}
