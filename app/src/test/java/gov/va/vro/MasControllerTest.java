package gov.va.vro;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.responses.MasClaimResponse;
import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.mas.ClaimDetail;
import gov.va.vro.model.mas.ClaimDetailConditions;
import gov.va.vro.model.mas.MasClaimDetailsPayload;
import gov.va.vro.model.mas.VeteranIdentifiers;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MasControllerTest extends BaseControllerTest {

  @EndpointInject("mock:mas-notification")
  private MockEndpoint mockMasNotificationEndpoint;

  @Autowired protected CamelContext camelContext;

  @Test
  @SneakyThrows
  void testJsonConversion() {
    ObjectMapper objectMapper = new ObjectMapper();
    var request =
        objectMapper.readValue(
            new ClassPathResource("mas-request.json").getFile(), MasClaimDetailsPayload.class);
    assertEquals("Rick", request.getFirstName());
    assertEquals("Smith", request.getLastName());
    assertEquals("123", request.getCollectionId());
    assertEquals("2002-12-12", request.getDateOfBirth());
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
    MasClaimDetailsPayload request =
        MasClaimDetailsPayload.builder().dateOfBirth("2002-12-12").collectionId("123").build();
    var responseEntity =
        post("/v1/notifyVROAutomatedClaimDetails", request, MasClaimResponse.class);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  void notifyAutomatedClaimDetails_validRequest() throws Exception {

    adviceWith(
        camelContext,
        "mas-claim-notification",
        route ->
            route
                .interceptSendToEndpoint(PrimaryRoutes.ENDPOINT_MAS)
                .skipSendToOriginalEndpoint()
                .to("mock:mas-notification"));
    // The mock endpoint returns a valid response
    mockMasNotificationEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.<MasClaimDetailsPayload, String>fromFunction(claim -> "hi"));
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

    MasClaimDetailsPayload request =
        MasClaimDetailsPayload.builder()
            .dateOfBirth("2002-12-12")
            .collectionId("123")
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
