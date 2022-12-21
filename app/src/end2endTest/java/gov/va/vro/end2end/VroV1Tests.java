package gov.va.vro.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import gov.va.vro.end2end.util.PdfText;
import gov.va.vro.end2end.util.RestHelper;
import gov.va.vro.end2end.util.TestSetup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class VroV1Tests {
  private void testAssessment(TestSetup setup, RestHelper helper) throws Exception {
    ResponseEntity<String> response = helper.getAssessment(setup);
    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

    String actual = response.getBody();
    String expected = setup.getAssessment();
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  private void testPdfGeneration(TestSetup setup, RestHelper helper) throws Exception {
    ResponseEntity<String> response = helper.generatePdf(setup);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    String actual = response.getBody();
    String expected = setup.getGeneratePdfResponse();
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  private void testGeneratedPdf(TestSetup setup, RestHelper helper) throws Exception {
    ResponseEntity<byte[]> response = helper.getPdf(setup);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    HttpHeaders responseHeaders = response.getHeaders();
    log.info("Response headers: {}", responseHeaders);
    ContentDisposition actualCd = responseHeaders.getContentDisposition();
    Assertions.assertEquals(setup.getContentDispositionFilename(), actualCd.getFilename());

    checkPdfText(setup, response.getBody());
  }

  private void checkPdfText(TestSetup setup, byte[] pdf) throws Exception {
    if (Boolean.parseBoolean(System.getenv("VRO_SAVE_PDF"))) {
      savePdfFile(pdf, setup.getName() + "-" + setup.getContentDispositionFilename());
    }
    PdfText pdfText = PdfText.getInstance(pdf);
    log.info("PDF text: {}", pdfText.getPdfText());

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

  private void savePdfFile(byte[] pdfContents, String filename) {
    try {
      Files.write(Paths.get(filename), pdfContents);
      log.info("Saved pdf to: {}", filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
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

  @Test
  public void attemptWithoutApiKey() throws Exception {
    TestSetup setup = TestSetup.getInstance("test-7101-01");

    RestHelper helper = new RestHelper();
    helper.setApiKey("bad-key-01");

    HttpClientErrorException.Unauthorized exception =
        assertThrowsExactly(
            HttpClientErrorException.Unauthorized.class, () -> helper.getAssessment(setup));
    assertEquals("401 : [no body]", exception.getMessage());
  }
}
