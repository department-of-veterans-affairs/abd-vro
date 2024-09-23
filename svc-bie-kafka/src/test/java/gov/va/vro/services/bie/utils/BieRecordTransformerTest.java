package gov.va.vro.services.bie.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
          .thenReturn("JournalStatusTypeCode");
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

    @ParameterizedTest
    @EnumSource(ContentionEvent.class)
    void testContentionEvent(ContentionEvent contentionEvent) throws JsonProcessingException {
      final String topic = contentionEvent.getTopicName();
      final ConsumerRecord<String, Object> record =
          new ConsumerRecord<>(topic, 0, 0, null, genericRecord);

      ContentionEventPayload actualPayload = bieRecordTransformer.toContentionEventPayload(record);
      assertFields(contentionEvent, actualPayload);
    }
  }

  @Nested
  class WithStringRecord {

    @ParameterizedTest
    @EnumSource(ContentionEvent.class)
    void testContentionEvent(ContentionEvent contentionEvent) throws JsonProcessingException {
      final String topic = contentionEvent.getTopicName();
      final String stringRecord =
          "{\"status\":200,\"eventType\":\"CONTENTION_UPDATED\",\"notifiedAt\":-1,\"claimId\":67890,\"contentionId\":11223,\"actionName\":\"SomeAction\",\"actionResultName\":\"ActionResult\",\"automationIndicator\":true,\"contentionTypeCode\":\"TypeCode456\",\"contentionStatusTypeCode\":\"StatusTypeCode\",\"currentLifecycleStatus\":\"LifecycleStatus\",\"eventTime\":1616151616,\"benefitClaimTypeCode\":\"TypeCode123\",\"actorStation\":\"Station456\",\"actorUserId\":\"UserId789\",\"details\":\"Some details\",\"veteranParticipantId\":12345,\"contentionClassificationName\":\"ClassificationName\",\"diagnosticTypeCode\":\"DiagnosticCode\",\"journalStatusTypeCode\":\"JournalStatusTypeCode\",\"dateAdded\":1616161616,\"dateCompleted\":1616171717,\"dateUpdated\":1616181818}";
      final ConsumerRecord<String, Object> record =
          new ConsumerRecord<>(topic, 0, 0, null, stringRecord);

      ContentionEventPayload actualPayload = bieRecordTransformer.toContentionEventPayload(record);
      assertFields(contentionEvent, actualPayload);
    }
  }

  @Nested
  class WithUnsupportedRecord {
    @Mock private ConsumerRecord<String, Object> intRecord;

    @ParameterizedTest
    @EnumSource(ContentionEvent.class)
    void testContentionEvent(ContentionEvent contentionEvent) {
      final String topic = contentionEvent.getTopicName();
      when(intRecord.topic()).thenReturn(topic);
      when(intRecord.value()).thenReturn(123);

      assertThrows(
          IllegalStateException.class,
          () -> bieRecordTransformer.toContentionEventPayload(intRecord));
    }
  }

  private void assertFields(ContentionEvent contentionEvent, ContentionEventPayload actualPayload) {
    switch (contentionEvent) {
      case CONTENTION_ASSOCIATED:
        assertEquals("TypeCode123", actualPayload.getBenefitClaimTypeCode());
        assertEquals("Station456", actualPayload.getActorStation());
        assertEquals("UserId789", actualPayload.getActorUserId());
        assertEquals("Some details", actualPayload.getDetails());
        assertEquals(Long.valueOf(12345L), actualPayload.getVeteranParticipantId());
        assertEquals("ClassificationName", actualPayload.getContentionClassificationName());
        assertEquals("DiagnosticCode", actualPayload.getDiagnosticTypeCode());
        assertEquals(Long.valueOf(1616161616L), actualPayload.getDateAdded());
        break;
      case CONTENTION_UPDATED:
        assertEquals("TypeCode123", actualPayload.getBenefitClaimTypeCode());
        assertEquals("JournalStatusTypeCode", actualPayload.getJournalStatusTypeCode());
        assertEquals("Station456", actualPayload.getActorStation());
        assertEquals("Some details", actualPayload.getDetails());
        assertEquals(Long.valueOf(12345L), actualPayload.getVeteranParticipantId());
        assertEquals("ClassificationName", actualPayload.getContentionClassificationName());
        assertEquals("DiagnosticCode", actualPayload.getDiagnosticTypeCode());
        assertEquals(Long.valueOf(1616161616L), actualPayload.getDateAdded());
        assertEquals(Long.valueOf(1616171717L), actualPayload.getDateCompleted());
        assertEquals(Long.valueOf(1616181818L), actualPayload.getDateUpdated());
        assertEquals(ContentionEvent.CONTENTION_UPDATED, actualPayload.getEventType());
        break;
      case CONTENTION_CLASSIFIED:
        assertEquals("TypeCode123", actualPayload.getBenefitClaimTypeCode());
        assertEquals("Station456", actualPayload.getActorStation());
        assertEquals("Some details", actualPayload.getDetails());
        assertEquals(Long.valueOf(12345L), actualPayload.getVeteranParticipantId());
        assertEquals("ClassificationName", actualPayload.getContentionClassificationName());
        assertEquals("DiagnosticCode", actualPayload.getDiagnosticTypeCode());
        assertEquals(Long.valueOf(1616161616L), actualPayload.getDateAdded());
        assertEquals(ContentionEvent.CONTENTION_CLASSIFIED, actualPayload.getEventType());
        break;
    }
    testCommonFields(actualPayload);
    assertEquals(contentionEvent, actualPayload.getEventType());
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
