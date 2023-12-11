package gov.va.vro.routes.xample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.model.biekafka.test.BieMessagePayloadFactory;
import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.ContentionEventEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.ContentionEventRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DbHelperTest {

  @Mock private ClaimRepository claimRepository;
  @Mock private VeteranRepository veteranRepository;
  @Mock private ContentionEventRepository contentionEventRepository;

  private DbHelper dbHelper;

  @BeforeEach
  void setupMocks() {
    lenient().doAnswer(returnsFirstArg()).when(claimRepository).save(any(ClaimEntity.class));
    lenient().doAnswer(returnsFirstArg()).when(veteranRepository).save(any(VeteranEntity.class));
    lenient()
        .doAnswer(returnsFirstArg())
        .when(contentionEventRepository)
        .save(any(ContentionEventEntity.class));
    dbHelper = new DbHelper(claimRepository, veteranRepository, contentionEventRepository);
  }

  private final SomeDtoModel someDtoModel =
      SomeDtoModel.builder().resourceId("320").diagnosticCode("B").build();

  @Test
  void testSaveToDb() {
    final ClaimEntity claimEntity = dbHelper.saveToDb(someDtoModel);

    assertNotNull(claimEntity.getVeteran());
    assertEquals(someDtoModel.getResourceId(), claimEntity.getVbmsId());
    assertEquals(
        someDtoModel.getDiagnosticCode(), claimEntity.getContentions().get(0).getDiagnosticCode());
  }

  @Test
  void saveContentionEvent() {
    final BieMessagePayload bieMessagePayload = BieMessagePayloadFactory.create();
    bieMessagePayload.setVeteranParticipantId(12345L);
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
  }
}
