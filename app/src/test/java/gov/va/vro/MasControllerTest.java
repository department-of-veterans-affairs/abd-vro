package gov.va.vro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.api.requests.MasClaimDetailsRequest;
import gov.va.vro.api.responses.MasClaimResponse;
import gov.va.vro.model.mas.ClaimDetail;
import gov.va.vro.model.mas.ClaimDetailConditions;
import gov.va.vro.model.mas.VeteranIdentifiers;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Disabled
public class MasControllerTest extends BaseControllerTest {

  @Test
  @SneakyThrows
  void testJsonConversion() {
    ObjectMapper objectMapper = new ObjectMapper();
    var request =
        objectMapper.readValue(
            new ClassPathResource("mas-request.json").getFile(), MasClaimDetailsRequest.class);
    assertEquals("Rick", request.getFirstName());
    assertEquals("Smith", request.getLastName());
    assertEquals("123", request.getCollectionsId());
    assertEquals("2002-12-12", request.getDob());

    assertEquals("X", request.getVeteranIdentifiers().getEdipn());
    assertEquals("X", request.getVeteranIdentifiers().getVeteranFileId());
    assertEquals("X", request.getVeteranIdentifiers().getParticipantId());
    assertEquals("X", request.getVeteranIdentifiers().getIcn());
    assertEquals("X", request.getVeteranIdentifiers().getSsn());
    assertEquals("1233", request.getClaimDetail().getConditions().getDiagnosticCode());
    assertEquals("VA.GOV", request.getClaimDetail().getClaimSubmissionSource());
  }

  @Test
  void notifyAutomatedClaimDetails_invalidRequest() {
    MasClaimDetailsRequest request =
        MasClaimDetailsRequest.builder().dob("2002-12-12").collectionsId("123").build();

    var responseEntity =
        post("/v1/notifyVROAutomatedClaimDetails", request, MasClaimResponse.class);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  void notifyAutomatedClaimDetails_validRequest() {
    VeteranIdentifiers veteranIdentifiers = new VeteranIdentifiers();
    veteranIdentifiers.setEdipn("X");
    veteranIdentifiers.setParticipantId("X");
    veteranIdentifiers.setIcn("X");
    veteranIdentifiers.setSsn("X");
    veteranIdentifiers.setVeteranFileId("X");
    ClaimDetailConditions conditions = new ClaimDetailConditions();
    conditions.setDiagnosticCode("1233");
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setConditions(conditions);

    MasClaimDetailsRequest request =
        MasClaimDetailsRequest.builder()
            .dob("2002-12-12")
            .collectionsId("123")
            .firstName("Rick")
            .lastName("Smith")
            .veteranIdentifiers(veteranIdentifiers)
            .claimDetail(claimDetail)
            .build();

    var responseEntity =
        post("/v1/notifyVROAutomatedClaimDetails", request, MasClaimResponse.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }
}
