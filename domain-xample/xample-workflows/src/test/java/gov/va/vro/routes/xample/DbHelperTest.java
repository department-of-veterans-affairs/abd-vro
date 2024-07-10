package gov.va.vro.routes.xample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.biekafka.test.BieMessagePayloadFactory;
import gov.va.vro.persistence.model.ContentionEventEntity;
import gov.va.vro.persistence.repository.ContentionEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DbHelperTest {

  @Mock private ContentionEventRepository contentionEventRepository;

  private DbHelper dbHelper;

  @BeforeEach
  void setupMocks() {
    lenient()
        .doAnswer(returnsFirstArg())
        .when(contentionEventRepository)
        .save(any(ContentionEventEntity.class));
    dbHelper = new DbHelper(contentionEventRepository);
  }

  @Test
  void saveContentionEvent() {
    final BieMessagePayload bieMessagePayload = BieMessagePayloadFactory.create();
    final ContentionEventEntity entity = dbHelper.saveContentionEvent(bieMessagePayload);

    assertNotNull(entity);
    assertEquals(bieMessagePayload.getEventType().toString(), entity.getEventType());
    assertEquals(bieMessagePayload.getClaimId(), entity.getClaimId());
    assertEquals(bieMessagePayload.getContentionId(), entity.getContentionId());
    assertEquals(bieMessagePayload.getContentionTypeCode(), entity.getContentionTypeCode());
    assertEquals(
        bieMessagePayload.getContentionClassificationName(),
        entity.getContentionClassificationName());
    assertEquals(bieMessagePayload.getDiagnosticTypeCode(), entity.getDiagnosticTypeCode());
    assertEquals(dbHelper.convertTime(bieMessagePayload.getEventTime()), entity.getOccurredAt());
    assertEquals(dbHelper.convertTime(bieMessagePayload.getNotifiedAt()), entity.getNotifiedAt());
    assertEquals(bieMessagePayload.getActionName(), entity.getActionName());
    assertEquals(bieMessagePayload.getActionResultName(), entity.getActionResultName());
  }
}
