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
        .build();
  }
}
