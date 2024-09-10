package gov.va.vro.services.bie.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BieRecordTransformerTest {

  private BieRecordTransformer bieRecordTransformer;

  @BeforeEach
  public void setUp() {
    // Set up the BieRecordTransformer
    this.bieRecordTransformer = new BieRecordTransformer();
  }

  @Nested
  class WithGenericRecord {
    @Mock private GenericRecord genericRecord;

    @BeforeEach
    void setUp() {
      // Set up a GenericRecord with test data
      Mockito.lenient().when(genericRecord.hasField(Mockito.anyString())).thenReturn(true);
      Mockito.lenient().when(genericRecord.get("BenefitClaimTypeCode")).thenReturn("TypeCode123");
      Mockito.lenient().when(genericRecord.get("ActorStation")).thenReturn("Station456");
      Mockito.lenient().when(genericRecord.get("ActorUserId")).thenReturn("UserId789");
      Mockito.lenient().when(genericRecord.get("Details")).thenReturn("Some details");
      Mockito.lenient()
          .when(genericRecord.get("VeteranParticipantId"))
          .thenReturn(12345L); // Example Long value
      Mockito.lenient().when(genericRecord.get("ClaimId")).thenReturn(67890L); // Example Long value
      Mockito.lenient()
          .when(genericRecord.get("ContentionId"))
          .thenReturn(11223L); // Example Long value
      Mockito.lenient().when(genericRecord.get("ContentionTypeCode")).thenReturn("TypeCode456");
      Mockito.lenient()
          .when(genericRecord.get("ContentionClassificationName"))
          .thenReturn("ClassificationName");
      Mockito.lenient().when(genericRecord.get("DiagnosticTypeCode")).thenReturn("DiagnosticCode");
      Mockito.lenient().when(genericRecord.get("ActionName")).thenReturn("SomeAction");
      Mockito.lenient().when(genericRecord.get("ActionResultName")).thenReturn("ActionResult");
      Mockito.lenient()
          .when(genericRecord.get("AutomationIndicator"))
          .thenReturn(true); // boolean value
      Mockito.lenient()
          .when(genericRecord.get("ContentionStatusTypeCode"))
          .thenReturn("StatusTypeCode");
      Mockito.lenient()
          .when(genericRecord.get("CurrentLifecycleStatus"))
          .thenReturn("LifecycleStatus");
      Mockito.lenient()
          .when(genericRecord.get("EventTime"))
          .thenReturn(1616151616L); // Example Long value
      Mockito.lenient()
          .when(genericRecord.get("JournalStatusTypeCode"))
          .thenReturn("JournalStatusCode");
      Mockito.lenient()
          .when(genericRecord.get("DateAdded"))
          .thenReturn(1616161616L); // Example Long value
      Mockito.lenient()
          .when(genericRecord.get("DateCompleted"))
          .thenReturn(1616171717L); // Example Long value
      Mockito.lenient()
          .when(genericRecord.get("DateUpdated"))
          .thenReturn(1616181818L); // Example Long value
    }

    @Test
    void testContentionAssociatedToClaimEvent() throws JsonProcessingException {
      final String topic = ContentionEvent.CONTENTION_ASSOCIATED.getTopicName();
      final ConsumerRecord<String, Object> record =
          new ConsumerRecord<>(topic, 0, 0, null, genericRecord);

      ContentionEventPayload actualPayload = bieRecordTransformer.toContentionEventPayload(record);
      testCommonFields(actualPayload);
      assertEquals("TypeCode123", actualPayload.getBenefitClaimTypeCode());
      assertEquals("Station456", actualPayload.getActorStation());
      assertEquals("UserId789", actualPayload.getActorUserId());
      assertEquals("Some details", actualPayload.getDetails());
      assertEquals(Long.valueOf(12345L), actualPayload.getVeteranParticipantId());
      assertEquals("ClassificationName", actualPayload.getContentionClassificationName());
      assertEquals("DiagnosticCode", actualPayload.getDiagnosticTypeCode());
      assertEquals(Long.valueOf(1616161616L), actualPayload.getDateAdded());
      assertEquals(ContentionEvent.CONTENTION_ASSOCIATED, actualPayload.getEventType());
    }

    @Test
    void testContentionClassifiedEvent() throws JsonProcessingException {
      final String topic = ContentionEvent.CONTENTION_CLASSIFIED.getTopicName();
      final ConsumerRecord<String, Object> record =
          new ConsumerRecord<>(topic, 0, 0, null, genericRecord);

      ContentionEventPayload actualPayload = bieRecordTransformer.toContentionEventPayload(record);
      testCommonFields(actualPayload);
      assertEquals("TypeCode123", actualPayload.getBenefitClaimTypeCode());
      assertEquals("Station456", actualPayload.getActorStation());
      assertEquals("Some details", actualPayload.getDetails());
      assertEquals(Long.valueOf(12345L), actualPayload.getVeteranParticipantId());
      assertEquals("ClassificationName", actualPayload.getContentionClassificationName());
      assertEquals("DiagnosticCode", actualPayload.getDiagnosticTypeCode());
      assertEquals(Long.valueOf(1616161616L), actualPayload.getDateAdded());
      assertEquals(ContentionEvent.CONTENTION_CLASSIFIED, actualPayload.getEventType());
    }

    @Test
    void testContentionCompletedEvent() throws JsonProcessingException {
      final String topic = ContentionEvent.CONTENTION_COMPLETED.getTopicName();
      final ConsumerRecord<String, Object> record =
          new ConsumerRecord<>(topic, 0, 0, null, genericRecord);

      ContentionEventPayload actualPayload = bieRecordTransformer.toContentionEventPayload(record);
      testCommonFields(actualPayload);
      assertEquals(ContentionEvent.CONTENTION_COMPLETED, actualPayload.getEventType());
    }

    @Test
    void testContentionDeletedEvent() throws JsonProcessingException {
      final String topic = ContentionEvent.CONTENTION_DELETED.getTopicName();
      final ConsumerRecord<String, Object> record =
          new ConsumerRecord<>(topic, 0, 0, null, genericRecord);

      ContentionEventPayload actualPayload = bieRecordTransformer.toContentionEventPayload(record);
      testCommonFields(actualPayload);
      assertEquals(ContentionEvent.CONTENTION_DELETED, actualPayload.getEventType());
    }

    @Test
    void testContentionUpdatedEvent() throws JsonProcessingException {
      // Process the mocked GenericRecord to create a BieMessagePayload
      final String topic = ContentionEvent.CONTENTION_UPDATED.getTopicName();
      final ConsumerRecord<String, Object> record =
          new ConsumerRecord<>(topic, 0, 0, null, genericRecord);

      ContentionEventPayload actualPayload = bieRecordTransformer.toContentionEventPayload(record);

      testCommonFields(actualPayload);

      assertEquals("TypeCode123", actualPayload.getBenefitClaimTypeCode());
      assertEquals("JournalStatusCode", actualPayload.getJournalStatusTypeCode());
      assertEquals("Station456", actualPayload.getActorStation());
      assertEquals("Some details", actualPayload.getDetails());
      assertEquals(Long.valueOf(12345L), actualPayload.getVeteranParticipantId());
      assertEquals("ClassificationName", actualPayload.getContentionClassificationName());
      assertEquals("DiagnosticCode", actualPayload.getDiagnosticTypeCode());
      assertEquals(Long.valueOf(1616161616L), actualPayload.getDateAdded());
      assertEquals(Long.valueOf(1616171717L), actualPayload.getDateCompleted());
      assertEquals(Long.valueOf(1616181818L), actualPayload.getDateUpdated());
      assertEquals(ContentionEvent.CONTENTION_UPDATED, actualPayload.getEventType());
    }

    private void testCommonFields(ContentionEventPayload actualPayload) {
      assertEquals(Long.valueOf(67890L), actualPayload.getClaimId());
      assertEquals(Long.valueOf(11223L), actualPayload.getContentionId());
      assertEquals("SomeAction", actualPayload.getActionName());
      assertEquals("ActionResult", actualPayload.getActionResultName());
      assertTrue(actualPayload.getAutomationIndicator());
      assertEquals("TypeCode456", actualPayload.getContentionTypeCode());
      assertEquals("StatusTypeCode", actualPayload.getContentionStatusTypeCode());
      assertEquals("LifecycleStatus", actualPayload.getCurrentLifecycleStatus());
      assertEquals(Long.valueOf(1616151616L), actualPayload.getEventTime());
    }
  }
}
