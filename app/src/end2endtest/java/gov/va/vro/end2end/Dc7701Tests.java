package gov.va.vro.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.end2end.util.PdfText;
import gov.va.vro.end2end.util.RestHelper;
import gov.va.vro.end2end.util.TestSetup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.Duration;

@Slf4j
public class Dc7701Tests {
  private ObjectMapper mapper = new ObjectMapper();

  @Test
  public void positive01() throws Exception {
    TestSetup setup = TestSetup.getInstance("test-7701-01", "7001");

    RestHelper helper = new RestHelper();
    helper.setApiKey("test-key-01");

    String actualAssess = helper.getAssessment(setup);
    String expectedAssess = setup.getAssessment();
    JSONAssert.assertEquals(expectedAssess, actualAssess, JSONCompareMode.STRICT);

    String pdfGenActual = helper.generatePdf(setup);
    String pdfGenExpected = setup.getGeneratePdfResponse();
    JSONAssert.assertEquals(pdfGenExpected, pdfGenActual, JSONCompareMode.STRICT);

    String cd = setup.getContentDisposition();

    byte[] actualBytes =
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:8080/v1")
            .defaultHeader("X-API-KEY", "test-key-01")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .responseTimeout(Duration.ofMillis(10000))
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/evidence-pdf/7001")
                .build())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .valueEquals("Content-Disposition", cd)
            .expectBody()
            .returnResult()
            .getResponseBody();

    PdfText pdfText = PdfText.getInstance(actualBytes);

    JsonNode bpReadings = setup.getBpReadingsNode();
    assertTrue(bpReadings.isArray());;
    int bpCount = pdfText.countBpReadings();
    assertEquals(bpReadings.size(), bpCount);

    JsonNode meds = setup.getMedicationsNode();
    assertTrue(meds.isArray());
    int medCount = pdfText.countMedications();
    assertEquals(meds.size(), medCount);

    JsonNode vetInfo = setup.getVeteranInfoNode();
    boolean hasVetInfo = pdfText.hasVeteranInfo(vetInfo);
    assertTrue(hasVetInfo);
  }
}
