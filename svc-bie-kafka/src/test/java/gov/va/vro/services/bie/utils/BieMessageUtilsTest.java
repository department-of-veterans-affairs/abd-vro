package gov.va.vro.services.bie.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import gov.va.vro.model.biekafka.BieMessagePayload;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BieMessageUtilsTest {

  @Mock private GenericRecord genericRecord;
  private BieMessagePayload expectedPayload;

  @BeforeEach
  void setUp() {
    // Set up a GenericRecord with test data
    when(genericRecord.get("BenefitClaimTypeCode")).thenReturn("TypeCode123");
    when(genericRecord.get("ActorStation")).thenReturn("Station456");
    when(genericRecord.get("Details")).thenReturn("Some details");
    when(genericRecord.get("VeteranParticipantId")).thenReturn(12345L); // Example Long value
    when(genericRecord.get("ClaimId")).thenReturn(67890L); // Example Long value
    when(genericRecord.get("ContentionId")).thenReturn(11223L); // Example Long value
    when(genericRecord.get("ContentionTypeCode")).thenReturn("TypeCode456");
    when(genericRecord.get("ContentionClassificationName")).thenReturn("ClassificationName");
    when(genericRecord.get("DiagnosticTypeCode")).thenReturn("DiagnosticCode");
    when(genericRecord.get("ActionName")).thenReturn("SomeAction");
    when(genericRecord.get("ActionResultName")).thenReturn("ActionResult");
    when(genericRecord.get("AutomationIndicator")).thenReturn(true); // boolean value
    when(genericRecord.get("ContentionStatusTypeCode")).thenReturn("StatusTypeCode");
    when(genericRecord.get("CurrentLifecycleStatus")).thenReturn("LifecycleStatus");
    when(genericRecord.get("EventTime")).thenReturn(1616151616L); // Example Long value
    when(genericRecord.get("ClmntTxt")).thenReturn("Claimant Text");
    when(genericRecord.get("JournalStatusTypeCode")).thenReturn("JournalStatusCode");
    when(genericRecord.get("DateAdded")).thenReturn(1616161616L); // Example Long value
    when(genericRecord.get("DateCompleted")).thenReturn(1616171717L); // Example Long value
    when(genericRecord.get("DateUpdated")).thenReturn(1616181818L); // Example Long value
    when(genericRecord.get("EventDetails")).thenReturn("Event Details Description");
  }

  @Test
  void testProcessBieMessagePayloadFields() {
    // Process the mocked GenericRecord to create a BieMessagePayload
    BieMessagePayload actualPayload = BieMessageUtils.processBieMessagePayloadFields(genericRecord);

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
    assertEquals("Claimant Text", actualPayload.getClmntTxt());
    assertEquals("JournalStatusCode", actualPayload.getJournalStatusTypeCode());
    assertEquals(Long.valueOf(1616161616L), actualPayload.getDateAdded());
    assertEquals(Long.valueOf(1616171717L), actualPayload.getDateCompleted());
    assertEquals(Long.valueOf(1616181818L), actualPayload.getDateUpdated());
    assertEquals("Event Details Description", actualPayload.getEventDetails());
  }
}
