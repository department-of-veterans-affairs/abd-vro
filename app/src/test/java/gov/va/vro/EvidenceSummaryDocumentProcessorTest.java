package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.requests.GeneratePdfRequest;
import gov.va.vro.api.responses.GeneratePdfResponse;
import gov.va.vro.config.AppTestConfig;
import gov.va.vro.persistence.model.ContentionEntity;
import gov.va.vro.persistence.model.EvidenceSummaryDocumentEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(AppTestConfig.class)
@CamelSpringBootTest
public class EvidenceSummaryDocumentProcessorTest extends BaseControllerTest {

  @Value("classpath:test-data/pdf-generator-input-01.json")
  private Resource pdfGeneratorInput01;

  @Autowired protected ClaimRepository claimRepository;

  @Test
  @DirtiesContext
  void positiveEvidenceSummaryDocumentProcessor() throws Exception {
    // Create veteran, claim, and contention and save.
    var veteran = TestDataSupplier.createVeteran("X", "Y");
    veteranRepository.save(veteran);
    ContentionEntity contention = new ContentionEntity("7101");
    var claim = TestDataSupplier.createClaim("1234", "type", veteran);
    claim.addContention(contention);
    claim = claimRepository.save(claim);

    // Call generate-pdf endpoint to activate the Evidence Summary Document processor.
    var mapper = new ObjectMapper();
    InputStream stream = pdfGeneratorInput01.getInputStream();
    String inputAsString = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    GeneratePdfRequest input = mapper.readValue(inputAsString, GeneratePdfRequest.class);
    var response = post("/v1/evidence-pdf", input, GeneratePdfResponse.class);

    // Verify that the evidence summary document is created and saved correctly.
    assert claim.getId() != null;
    var claim2 = claimRepository.findById(claim.getId()).orElse(null);
    assert claim2 != null;
    assertEquals(claim2.getContentions().get(0).getEvidenceSummaryDocuments().size(), 1);
    EvidenceSummaryDocumentEntity evidenceSummaryDocument =
        claim2.getContentions().get(0).getEvidenceSummaryDocuments().get(0);
    assertEquals(evidenceSummaryDocument.getEvidenceCount().get("medicationsCount"), "2");
    assertEquals(evidenceSummaryDocument.getEvidenceCount().get("totalBpReadings"), "3");
    String timestamp = String.format("%1$tY%1$tm%1$td", new Date());
    String diagnosis = "Hypertension";
    String documentName =
        String.format("VAMC_%s_Rapid_Decision_Evidence--%s.pdf", diagnosis, timestamp);
    assertEquals(evidenceSummaryDocument.getDocumentName(), documentName);
    assertEquals(evidenceSummaryDocument.getContention().getId(), contention.getId());
  }

  @Test
  @DirtiesContext
  void negativeEvidenceSummaryDocumentProcessorWrongDiagnosticCode() throws Exception {
    // Create veteran, claim, and contention and then save.
    var veteran = TestDataSupplier.createVeteran("X", "Y");
    veteranRepository.save(veteran);
    ContentionEntity contention = new ContentionEntity("c1");

    // Set an invalid diagnostic code.
    contention.setDiagnosticCode("1111");
    var claim = TestDataSupplier.createClaim("1234", "type", veteran);
    claim.addContention(contention);
    claim = claimRepository.save(claim);

    // Call generate-pdf endpoint to activate the Evidence Summary Document processor.
    var mapper = new ObjectMapper();
    var mockResponseObj = new GeneratePdfResponse("1234", "7701", "COMPLETE");
    String mockResponse = mapper.writeValueAsString(mockResponseObj);
    InputStream stream = pdfGeneratorInput01.getInputStream();
    String inputAsString = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    GeneratePdfRequest input = mapper.readValue(inputAsString, GeneratePdfRequest.class);
    var response = post("/v1/evidence-pdf", input, GeneratePdfResponse.class);

    // Verify that the evidence summary document is not created or saved.
    assert claim.getId() != null;
    var claim2 = claimRepository.findById(claim.getId()).orElse(null);
    assert claim2 != null;
    assertEquals(claim2.getContentions().get(0).getEvidenceSummaryDocuments().size(), 0);
  }
}
