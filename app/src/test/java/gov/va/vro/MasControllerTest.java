package gov.va.vro;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.api.responses.MasClaimResponse;
import gov.va.vro.camel.FunctionProcessor;
import gov.va.vro.model.mas.ClaimCondition;
import gov.va.vro.model.mas.ClaimDetail;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.model.mas.VeteranIdentifiers;
import gov.va.vro.service.event.AuditEventProcessor;
import gov.va.vro.service.provider.camel.PrimaryRoutes;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MasControllerTest extends BaseControllerTest {

  @EndpointInject("mock:mas-notification")
  private MockEndpoint mockMasNotificationEndpoint;

  @Autowired private CamelContext camelContext;

  @Autowired @SpyBean private AuditEventProcessor auditEventProcessor;

  @Test
  @SneakyThrows
  void testJsonConversion() {
    ObjectMapper objectMapper = new ObjectMapper();
    var request =
        objectMapper.readValue(
            new ClassPathResource("mas-request.json").getFile(), MasAutomatedClaimPayload.class);
    assertEquals("Rick", request.getFirstName());
    assertEquals("Smith", request.getLastName());
    assertEquals(123, request.getCollectionId());
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
  void automatedClaimD_invalidRequest() {
    MasAutomatedClaimPayload request =
        MasAutomatedClaimPayload.builder().dateOfBirth("2002-12-12").collectionId(123).build();
    var responseEntity = post("/v1/automatedClaim", request, MasClaimResponse.class);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  void automatedClaim_validRequest() throws Exception {

    adviceWith(
            camelContext,
            "mas-claim-notification",
            route ->
                route
                    .interceptSendToEndpoint(PrimaryRoutes.ENDPOINT_MAS)
                    .skipSendToOriginalEndpoint()
                    .to("mock:mas-notification"))
        .end();
    // The mock endpoint returns a valid response
    mockMasNotificationEndpoint.whenAnyExchangeReceived(
        FunctionProcessor.<MasAutomatedClaimPayload, String>fromFunction(claim -> "hi"));
    MasAutomatedClaimPayload request = getMasAutomatedClaimPayload();
    var responseEntity = post("/v1/automatedClaim", request, MasClaimResponse.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  void automatedClaim_throwsException() throws Exception {

    adviceWith(
            camelContext,
            "mas-claim-notification",
            route ->
                route
                    .interceptSendToEndpoint(PrimaryRoutes.ENDPOINT_MAS)
                    .throwException(new Exception()))
        .end();

    MasAutomatedClaimPayload request = getMasAutomatedClaimPayload();
    post("/v1/automatedClaim", request, MasClaimResponse.class);
    Mockito.verify(auditEventProcessor)
        .logException(Mockito.any(Object.class), Mockito.any(Throwable.class), Mockito.anyString());
  }

  private static MasAutomatedClaimPayload getMasAutomatedClaimPayload() {
    VeteranIdentifiers veteranIdentifiers = new VeteranIdentifiers();
    veteranIdentifiers.setEdipn("X");
    veteranIdentifiers.setParticipantId("X");
    veteranIdentifiers.setIcn("X");
    veteranIdentifiers.setSsn("X");
    veteranIdentifiers.setVeteranFileId("X");
    ClaimCondition conditions = new ClaimCondition();
    conditions.setDiagnosticCode("1233");
    ClaimDetail claimDetail = new ClaimDetail();
    claimDetail.setClaimSubmissionDateTime("123");
    claimDetail.setConditions(conditions);

    return MasAutomatedClaimPayload.builder()
        .dateOfBirth("2002-12-12")
        .collectionId(123)
        .firstName("Rick")
        .lastName("Smith")
        .veteranIdentifiers(veteranIdentifiers)
        .claimDetail(claimDetail)
        .build();
  }
}
