package gov.va.vro.services.bie.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import gov.va.vro.persistence.model.bieevents.ContentionEventEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentionEventPayloadTransformerTest {

  private ContentionEventPayloadTransformer transformer;

  @BeforeEach
  void setUp() {
    transformer = new ContentionEventPayloadTransformer();
  }

  @Test
  void transformsPayloadToEntity() {
    ContentionEventPayload payload =
        ContentionEventPayload.builder()
            .claimId(123L)
            .contentionId(456L)
            .eventType(ContentionEvent.CONTENTION_ASSOCIATED)
            .notifiedAt(1616161616L)
            .contentionTypeCode("TypeCode")
            .diagnosticTypeCode("DiagCode")
            .contentionClassificationName("Classification")
            .eventTime(1616161616L)
            .dateAdded(1616161616L)
            .dateCompleted(1616171717L)
            .dateUpdated(1616181818L)
            .actorStation("Station")
            .actorUserId("UserId")
            .automationIndicator(true)
            .benefitClaimTypeCode("BenefitCode")
            .contentionStatusTypeCode("StatusCode")
            .currentLifecycleStatus("LifecycleStatus")
            .details("Details")
            .journalStatusTypeCode("JournalCode")
            .veteranParticipantId(789L)
            .actionName("Action")
            .actionResultName("Result")
            .description("Description")
            .build();

    ContentionEventEntity entity = transformer.toContentionEventEntity(payload);

    assertEquals(123L, entity.getClaimId());
    assertEquals(456L, entity.getContentionId());
    assertEquals("CONTENTION_ASSOCIATED", entity.getEventType());
    assertEquals("TypeCode", entity.getContentionTypeCode());
    assertEquals("DiagCode", entity.getDiagnosticTypeCode());
    assertEquals("Classification", entity.getContentionClassificationName());
    assertEquals("Station", entity.getActorStation());
    assertEquals("UserId", entity.getActorUserId());
    assertTrue(entity.isAutomationIndicator());
    assertEquals("BenefitCode", entity.getBenefitClaimTypeCode());
    assertEquals("StatusCode", entity.getContentionStatusTypeCode());
    assertEquals("LifecycleStatus", entity.getCurrentLifecycleStatus());
    assertEquals("Details", entity.getDetails());
    assertEquals("JournalCode", entity.getJournalStatusTypeCode());
    assertEquals(789L, entity.getVeteranParticipantId());
    assertEquals("Action", entity.getActionName());
    assertEquals("Result", entity.getActionResultName());
    assertEquals("Description", entity.getDescription());
  }
}
