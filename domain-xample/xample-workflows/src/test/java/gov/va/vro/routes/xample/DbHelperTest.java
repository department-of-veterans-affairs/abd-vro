package gov.va.vro.routes.xample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import gov.va.vro.model.xample.SomeDtoModel;
import gov.va.vro.persistence.model.ClaimEntity;
import gov.va.vro.persistence.model.VeteranEntity;
import gov.va.vro.persistence.repository.ClaimRepository;
import gov.va.vro.persistence.repository.VeteranRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DbHelperTest {
  @Mock ClaimRepository claimRepository;
  @Mock VeteranRepository veteranRepository;

  DbHelper dbHelper;

  @BeforeEach
  void setupMocks() {
    doAnswer(returnsFirstArg()).when(claimRepository).save(any(ClaimEntity.class));
    doAnswer(returnsFirstArg()).when(veteranRepository).save(any(VeteranEntity.class));
    dbHelper = new DbHelper(claimRepository, veteranRepository);
  }

  SomeDtoModel someDtoModel = SomeDtoModel.builder().resourceId("320").diagnosticCode("B").build();

  @Test
  void testSaveToDb() {
    ClaimEntity claimEntity = dbHelper.saveToDb(someDtoModel);

    assertNotNull(claimEntity.getVeteran());
    assertEquals(someDtoModel.getResourceId(), claimEntity.getVbmsId());
    assertEquals(
        someDtoModel.getDiagnosticCode(), claimEntity.getContentions().get(0).getDiagnosticCode());
  }
}
