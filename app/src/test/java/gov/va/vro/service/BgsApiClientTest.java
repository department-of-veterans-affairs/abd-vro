package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static gov.va.vro.service.provider.bgs.service.BgsClaimNotes.OFFRAMP_ERROR_2_CLAIM_NOTE;

import gov.va.vro.MasTestData;
import gov.va.vro.model.bgs.BgsApiClientRequest;
import gov.va.vro.persistence.repository.EvidenceSummaryDocumentRepository;
import gov.va.vro.service.provider.bgs.service.BgsApiClient;
import gov.va.vro.service.provider.bgs.service.BgsClaimNotes;

import gov.va.vro.service.provider.bgs.service.BgsNotesCamelBody;
import gov.va.vro.service.provider.mas.MasCamelStage;
import gov.va.vro.service.provider.mas.MasCompletionStatus;
import gov.va.vro.service.provider.mas.MasProcessingObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import java.util.UUID;

import org.apache.camel.test.junit5.params.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BgsApiClientTest {

    private BgsApiClient bgsApiClient;

    private final ObjectMapper objectMapper = new ObjectMapper();    

    private BgsNotesCamelBody GetBgsNoteBody(MasProcessingObject mpo) {
      return new BgsNotesCamelBody(mpo, 10000);
    }

    private MasCompletionStatus GetBgsStatus(MasProcessingObject mpo) {
      return MasCompletionStatus.of(mpo);
    }

    private MasProcessingObject GetMasProcessingObject() {
      var payload = MasTestData.getMasAutomatedClaimPayload(123, "1701", "345");
      var mpo = new MasProcessingObject(payload, MasCamelStage.START_COMPLETE);
      return mpo;
    }


    @Test
    public void testBuildRequest_ReadyForDecision() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

      BgsNotesCamelBody body = GetBgsNoteBody(GetMasProcessingObject());
      MasCompletionStatus status = GetBgsStatus(GetMasProcessingObject());

    
      assertEquals(MasCompletionStatus.READY_FOR_DECISION, status.getDescription());
      assertEquals(BgsClaimNotes.RFD_NOTE, body.pendingRequests.get(0).getClaimNotes().get(0));
      assertEquals(BgsClaimNotes.ARSD_COMPLETED_NOTE, body.pendingRequests.get(0).getClaimNotes().get(1));
    }

    @Test
    public void testBuildRequest_ExamOrder() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    
      BgsNotesCamelBody body = GetBgsNoteBody(GetMasProcessingObject());
      MasCompletionStatus status = GetBgsStatus(GetMasProcessingObject());

    
      assertEquals(MasCompletionStatus.EXAM_ORDER, status.getDescription());
      assertEquals(BgsClaimNotes.EXAM_REQUESTED_NOTE, body.pendingRequests.get(0).getClaimNotes().get(0));
    }

    @Test
    public void testBuildRequest_offRamp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

      BgsNotesCamelBody body = GetBgsNoteBody(GetMasProcessingObject());
      MasCompletionStatus status = GetBgsStatus(GetMasProcessingObject());

    
      assertEquals(MasCompletionStatus.OFF_RAMP, status.getDescription());
      assertEquals(BgsClaimNotes.EXAM_REQUESTED_NOTE, body.pendingRequests.get(0).getClaimNotes().get(0));
    }

    @Test
    public void testBuildRequest_offRamp_flash266() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        
      BgsNotesCamelBody body = GetBgsNoteBody(GetMasProcessingObject());
      MasCompletionStatus status = GetBgsStatus(GetMasProcessingObject());

    
      assertEquals(MasCompletionStatus.OFF_RAMP, status.getDescription());
      assertEquals(BgsClaimNotes.EXAM_REQUESTED_NOTE, body.pendingRequests.get(0).getClaimNotes().get(0));
    }

    @Test
    public void testBuildRequest_offRamp_sufficiencyIssue() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
      
      BgsNotesCamelBody body = GetBgsNoteBody(GetMasProcessingObject());
      MasCompletionStatus status = GetBgsStatus(GetMasProcessingObject());

    
      assertEquals(MasCompletionStatus.OFF_RAMP, status.getDescription());
      assertEquals(BgsClaimNotes.EXAM_REQUESTED_NOTE, body.pendingRequests.get(0).getClaimNotes().get(0));
    }

    @Test
    public void testBuildRequest_offRamp_pdfUploadFail() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
      
      BgsNotesCamelBody body = GetBgsNoteBody(GetMasProcessingObject());
      MasCompletionStatus status = GetBgsStatus(GetMasProcessingObject());

    
      assertEquals(MasCompletionStatus.OFF_RAMP, status.getDescription());
      assertEquals(BgsClaimNotes.EXAM_REQUESTED_NOTE, body.pendingRequests.get(0).getClaimNotes().get(0));
    }
} 