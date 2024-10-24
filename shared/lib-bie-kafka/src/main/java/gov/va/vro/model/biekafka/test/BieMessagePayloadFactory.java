package gov.va.vro.model.biekafka.test;

import gov.va.vro.model.biekafka.ContentionEvent;
import gov.va.vro.model.biekafka.ContentionEventPayload;
import net.datafaker.Faker;

import java.util.concurrent.TimeUnit;

public class BieMessagePayloadFactory {
  private static final Faker faker = new Faker();

  private static ContentionEventPayload createPayload(ContentionEvent eventType) {
    return ContentionEventPayload.builder()
        .eventType(eventType)
        .claimId(faker.random().nextLong())
        .contentionClassificationName(faker.lorem().word())
        .contentionTypeCode(faker.lorem().characters(10))
        .contentionId(faker.random().nextLong())
        .diagnosticTypeCode(faker.lorem().characters(10))
        .eventTime(faker.date().past(60, TimeUnit.DAYS).getTime())
        .notifiedAt(faker.date().past(60, TimeUnit.DAYS).getTime())
        .actionName(faker.lorem().characters(10))
        .actionResultName(faker.lorem().characters(10))
        .status(200)
        .build();
  }

  private static void setCommonPayloadValues(ContentionEventPayload payload) {
    payload.setBenefitClaimTypeCode(faker.lorem().characters(10));
    payload.setActorStation(faker.lorem().characters(200));
    payload.setDetails(faker.lorem().characters(200));
    payload.setVeteranParticipantId(faker.random().nextLong());
    payload.setAutomationIndicator(faker.bool().bool());
    payload.setContentionStatusTypeCode(faker.lorem().characters(5));
    payload.setCurrentLifecycleStatus(faker.lorem().characters(8));
    payload.setEventTime(faker.date().past(60, TimeUnit.DAYS).getTime());
  }

  private static void setDeletedPayloadValues(ContentionEventPayload payload) {
    payload.setAutomationIndicator(faker.bool().bool());
    payload.setContentionStatusTypeCode(faker.lorem().characters(5));
    payload.setCurrentLifecycleStatus(faker.lorem().characters(8));
    payload.setEventTime(faker.date().past(60, TimeUnit.DAYS).getTime());
  }

  public static ContentionEventPayload create() {
    // generate a random contention classification event type
    ContentionEvent eventType = faker.options().option(ContentionEvent.values());

    ContentionEventPayload payload = createPayload(eventType);

    switch (eventType) {
      case CONTENTION_ASSOCIATED:
      case CONTENTION_CLASSIFIED:
        setCommonPayloadValues(payload);
        break;
      case CONTENTION_UPDATED:
        setCommonPayloadValues(payload);
        payload.setJournalStatusTypeCode(faker.lorem().characters(5));
        break;
      case CONTENTION_DELETED:
        setDeletedPayloadValues(payload);
        break;
      case CONTENTION_COMPLETED:
        break;
    }

    return payload;
  }
}
