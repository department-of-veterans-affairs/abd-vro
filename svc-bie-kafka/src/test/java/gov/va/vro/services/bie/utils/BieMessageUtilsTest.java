package gov.va.vro.services.bie.utils;

import static org.junit.jupiter.api.Assertions.*;

import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.biekafka.ContentionEvent;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BieMessageUtilsTest {

  @Mock private GenericRecord genericRecord;
  private BieMessagePayload expectedPayload;

  @BeforeEach
  void setUp() {
    // Set up a GenericRecord with test data
    Mockito.lenient().when(genericRecord.get("BenefitClaimTypeCode")).thenReturn("TypeCode123");
    Mockito.lenient().when(genericRecord.get("ActorStation")).thenReturn("Station456");
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
  void testProcessPayload() {
    // Process the mocked GenericRecord to create a BieMessagePayload
    BieMessagePayload actualPayload =
        BieMessageUtils.processBieMessagePayloadFields(
            ContentionEvent.CONTENTION_ASSOCIATED_TO_CLAIM, genericRecord);

    // Assert that the fields in the actualPayload match the mocked values
    assertEquals("TypeCode123", actualPayload.getBenefitClaimTypeCode());
    assertEquals("Station456", actualPayload.getActorStation());
    assertEquals("Some details", actualPayload.getDetails());
    assertEquals(Long.valueOf(12345L), actualPayload.getVeteranParticipantId());
    assertEquals(Long.valueOf(67890L), actualPayload.getClaimId());
    assertEquals(Long.valueOf(11223L), actualPayload.getContentionId());
    assertEquals("TypeCode456", actualPayload.getContentionTypeCode());
    assertEquals("ClassificationName", actualPayload.getContentionClassificationName());
    assertEquals("DiagnosticCode", actualPayload.getDiagnosticTypeCode());
    assertEquals("SomeAction", actualPayload.getActionName());
    assertEquals("ActionResult", actualPayload.getActionResultName());
    assertTrue(actualPayload.getAutomationIndicator());
    assertEquals("StatusTypeCode", actualPayload.getContentionStatusTypeCode());
    assertEquals("LifecycleStatus", actualPayload.getCurrentLifecycleStatus());
    assertEquals(Long.valueOf(1616151616L), actualPayload.getEventTime());
    assertNull(actualPayload.getJournalStatusTypeCode());
    assertEquals(Long.valueOf(1616161616L), actualPayload.getDateAdded());
    assertEquals(Long.valueOf(1616171717L), actualPayload.getDateCompleted());
    assertEquals(Long.valueOf(1616181818L), actualPayload.getDateUpdated());
  }

  @Test
  void testProcessPayloadForContentionUpdatedEvent() {
    // Process the mocked GenericRecord to create a BieMessagePayload
    BieMessagePayload actualPayload =
        BieMessageUtils.processBieMessagePayloadFields(
            ContentionEvent.CONTENTION_UPDATED, genericRecord);

    // Assert that the fields in the actualPayload match the mocked values
    assertEquals("JournalStatusCode", actualPayload.getJournalStatusTypeCode());
  }
}
