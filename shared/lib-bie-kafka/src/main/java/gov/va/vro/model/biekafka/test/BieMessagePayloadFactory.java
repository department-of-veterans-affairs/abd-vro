package gov.va.vro.model.biekafka.test;

import com.google.common.collect.ImmutableMap;
import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.biekafka.ContentionEvent;
import net.datafaker.Faker;

import java.util.concurrent.TimeUnit;

public class BieMessagePayloadFactory {

  private static final Faker faker = new Faker();

  public static BieMessagePayload create() {
    return BieMessagePayload.builder()
        .eventType(ContentionEvent.CONTENTION_ASSOCIATED_TO_CLAIM)
        .claimId(faker.random().nextLong())
        .contentionClassificationName(faker.lorem().word())
        .contentionTypeCode(faker.lorem().characters(10))
        .contentionId(faker.random().nextLong())
        .diagnosticTypeCode(faker.lorem().characters(10))
        .occurredAt(faker.date().past(60, TimeUnit.DAYS).getTime())
        .notifiedAt(faker.date().past(60, TimeUnit.DAYS).getTime())
        .status(200)
        .eventDetails(
            ImmutableMap.<String, Object>builder()
                .put("actionName", faker.lorem().word())
                .put("actionResultName", faker.lorem().word())
                .put("actorApplicationId", faker.lorem().characters(8))
                .put("actorStation", faker.lorem().characters(8))
                .put("actorUserId", faker.lorem().characters(8))
                .put("automationIndicator", faker.bool().bool())
                .put("benefitClaimTypeCode", faker.lorem().characters(10))
                .put("contentionTypeCode", faker.lorem().characters(10))
                .put("contentionStatusTypeCode", faker.lorem().characters(10))
                .put("currentLifecycleStatus", faker.lorem().characters(10))
                .put("dateAdded", faker.date().past(365, TimeUnit.DAYS).getTime())
                .put("details", faker.lorem().sentence())
                .put("veteranParticipantId", faker.number().randomNumber())
                .put("bieTs", faker.date().past(365 * 20, TimeUnit.DAYS).getTime())
                .put("sourceTs", faker.date().past(365 * 20, TimeUnit.DAYS).getTime())
                .put("connectorTs", faker.date().past(365 * 20, TimeUnit.DAYS).getTime())
                .build())
        .build();
  }
}
