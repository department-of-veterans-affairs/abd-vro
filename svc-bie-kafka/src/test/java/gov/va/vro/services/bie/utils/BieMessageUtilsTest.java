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
    when(genericRecord.get("ClaimId")).thenReturn(12345L);
    when(genericRecord.get("VeteranParticipantId")).thenReturn(null);
    when(genericRecord.get("Details")).thenReturn("detail here");

    // Set up the expected BieMessagePayload object
    expectedPayload =
        BieMessagePayload.builder()
            .benefitClaimTypeCode("TypeCode123")
            .actorStation("Station456")
            .claimId(12345L)
            .details("detail here")
            .build();
  }

  @Test
  void testProcessBieMessagePayloadFields() {
    // Process the GenericRecord to create a BieMessagePayload
    BieMessagePayload actualPayload = BieMessageUtils.processBieMessagePayloadFields(genericRecord);

    // Assert that the fields in the actualPayload match those in expectedPayload
    assertEquals(
        expectedPayload.getBenefitClaimTypeCode(), actualPayload.getBenefitClaimTypeCode());
    assertEquals(expectedPayload.getActorStation(), actualPayload.getActorStation());
    assertEquals(expectedPayload.getClaimId(), actualPayload.getClaimId());
    assertEquals(expectedPayload.getDetails(), actualPayload.getDetails());
    assertEquals(
        expectedPayload.getVeteranParticipantId(), actualPayload.getVeteranParticipantId());
  }
}
