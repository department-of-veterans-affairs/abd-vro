package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import gov.va.vro.model.bgs.BgsApiClientRequest;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
import gov.va.vro.service.provider.bgs.service.BgsNotesCamelBody;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import java.util.UUID;

import org.apache.camel.test.junit5.params.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

@Test
@Slf4j
public class BgsApiClientTest {

    private BgsApiClient bgsApiClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    public void testBuildRequest_ReadyForDecision() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        MasProcessingObject mpo = mock(MasProcessingObject.class, "ready for decision");

        Field benefitClaimIdField = mpo.getClass().getDeclaredField("benefitClaimId");
        benefitClaimIdField.setAccessible(true);
        benefitClaimIdField.set(mpo, "3010");
        mpo.getClaimPayload().getVeteranIdentifiers().setParticipantId("3010");
        mpo.getClaimPayload().setEvidenceSummaryDocumentId(UUID.randomUUID());

        BgsNotesCamelBody body = new BgsNotesCamelBody(mpo, 100);

        body = bgsApiClient.buildRequest(mpo);

        // Verify that the veteran note request is included in the body.
        assertEquals(1, body.pendingRequests.size());
        BgsApiClientRequest veteranNoteRequest = body.pendingRequests.get(0);
        assertEquals("3010", veteranNoteRequest.getVbmsClaimId());
        assertEquals("ARSD_COMPLETED_NOTE", veteranNoteRequest.veteranNote);

        // Verify that the claim note request is included in the body.
        assertEquals(1, body.pendingRequests.size());
        BgsApiClientRequest claimNoteRequest = body.pendingRequests.get(0);
        assertEquals("3010", claimNoteRequest.getVbmsClaimId());
        assertEquals("RFD_NOTE", claimNoteRequest.claimNotes.get(0));
    }

@Test
public void testBuildRequest_ExamOrder() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    MasProcessingObject mpo = mock(MasProcessingObject.class, "exam order");

    Field benefitClaimIdField = mpo.getClass().getDeclaredField("benefitClaimId");
    benefitClaimIdField.setAccessible(true);
    benefitClaimIdField.set(mpo, "3010");
    mpo.getClaimPayload().getVeteranIdentifiers().setParticipantId("3010");
    mpo.getClaimPayload().setEvidenceSummaryDocumentId(UUID.randomUUID());

    BgsNotesCamelBody body = new BgsNotesCamelBody(mpo, 100);

    body = bgsApiClient.buildRequest(mpo);

  // Verify that the veteran note request is included in the body.
  assertEquals(1, body.pendingRequests.size());
  BgsApiClientRequest veteranNoteRequest = body.pendingRequests.get(0);
  assertEquals("123456789", veteranNoteRequest.getVbmsClaimId());
  assertEquals("ARSD_COMPLETED_NOTE", veteranNoteRequest.veteranNote);

  // Verify that the claim note request is included in the body.
  assertEquals(1, body.pendingRequests.size());
  BgsApiClientRequest claimNoteRequest = body.pendingRequests.get(0);
  assertEquals("123456789", claimNoteRequest.getVbmsClaimId());
  assertEquals("EXAM_REQUESTED_NOTE", claimNoteRequest.claimNotes.get(0));
}

@Test
public void testBuildRequest_OffRamp_flash266() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    MasProcessingObject mpo = mock(MasProcessingObject.class, "off-ramp");

    Field benefitClaimIdField = mpo.getClass().getDeclaredField("benefitClaimId");
    benefitClaimIdField.setAccessible(true);
    benefitClaimIdField.set(mpo, "3010");
    mpo.getClaimPayload().getVeteranIdentifiers().setParticipantId("3010");
    mpo.getClaimPayload().setEvidenceSummaryDocumentId(UUID.randomUUID());

    BgsNotesCamelBody body = new BgsNotesCamelBody(mpo, 100);

    body = bgsApiClient.buildRequest(mpo);

  // Verify that the claim note request is included in the body.
  assertEquals(1, body.pendingRequests.size());
  BgsApiClientRequest claimNoteRequest = body.pendingRequests.get(0);
  assertEquals("123456789", claimNoteRequest.getVbmsClaimId());
  assertEquals("newClaimMissingFlash266", claimNoteRequest.claimNotes.get(0));
}

@Test
public void testBuildRequest_OffRamp_sufficiencyIssue() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    MasProcessingObject mpo = mock(MasProcessingObject.class, "off-ramp");

    Field benefitClaimIdField = mpo.getClass().getDeclaredField("benefitClaimId");
    benefitClaimIdField.setAccessible(true);
    benefitClaimIdField.set(mpo, "3010");
    mpo.getClaimPayload().getVeteranIdentifiers().setParticipantId("3010");
    mpo.getClaimPayload().setEvidenceSummaryDocumentId(UUID.randomUUID());

    BgsNotesCamelBody body = new BgsNotesCamelBody(mpo, 100);

    body = bgsApiClient.buildRequest(mpo);

  // Verify that the claim note request is included in the body.
  assertEquals(1, body.pendingRequests.size());
  BgsApiClientRequest claimNoteRequest = body.pendingRequests.get(0);
  assertEquals("123456789", claimNoteRequest.getVbmsClaimId());
  assertEquals("Sufficiency cannot be determined.", claimNoteRequest.claimNotes.get(0));
}

@Test
public void testBuildRequest_OffRamp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    MasProcessingObject mpo = mock(MasProcessingObject.class, "off-ramp");

    Field benefitClaimIdField = mpo.getClass().getDeclaredField("benefitClaimId");
    benefitClaimIdField.setAccessible(true);
    benefitClaimIdField.set(mpo, "3010");
    mpo.getClaimPayload().getVeteranIdentifiers().setParticipantId("3010");
    mpo.getClaimPayload().setEvidenceSummaryDocumentId(UUID.randomUUID());

    BgsNotesCamelBody body = new BgsNotesCamelBody(mpo, 100);

    body = bgsApiClient.buildRequest(mpo);

  // Verify that the claim note request is included in the body.
  assertEquals(1, body.pendingRequests.size());
  BgsApiClientRequest claimNoteRequest = body.pendingRequests.get(0);
  assertEquals("123456789", claimNoteRequest.getVbmsClaimId());
  assertEquals("newClaimMissingFlash266", claimNoteRequest.claimNotes.get(0));
}

public static org.slf4j.Logger getLog() {
    return log;
}


public ObjectMapper getObjectMapper() {
    return objectMapper;
}
}    