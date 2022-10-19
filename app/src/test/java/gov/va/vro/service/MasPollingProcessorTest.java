package gov.va.vro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.BaseIntegrationTest;
import gov.va.vro.model.event.EventType;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.aspect.EventLog;
import gov.va.vro.service.provider.MasPollingProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MasPollingProcessorTest extends BaseIntegrationTest {

  @Autowired MasPollingProcessor masPollingProcessor;

  @Autowired EventLog eventLog;

  @Test
  void test() {
    var payload = MasAutomatedClaimPayload.builder().collectionId(123).build();
    masPollingProcessor.process(payload);

    var events = eventLog.getEvents();
    assertEquals(2, events.size());
    var startEvent = events.get(0);
    assertEquals("123", startEvent.getEventId());
    assertEquals(EventType.AUTOMATED_CLAIM, startEvent.getEventType());
    assertEquals("MasAutomatedClaimPayload", startEvent.getQualifier());
    assertEquals(
        "Entering gov.va.vro.service.provider.MasPollingProcessor.process",
        startEvent.getMessage());
    assertNotNull(startEvent.getEventTime());

    var endEvent = events.get(1);
    assertEquals("123", endEvent.getEventId());
    assertEquals(EventType.AUTOMATED_CLAIM, endEvent.getEventType());
    assertEquals("MasAutomatedClaimPayload", endEvent.getQualifier());
    assertEquals(
        "Exiting gov.va.vro.service.provider.MasPollingProcessor.process", endEvent.getMessage());
    assertNotNull(endEvent.getEventTime());
  }
}
