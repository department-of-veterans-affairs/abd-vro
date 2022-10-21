package gov.va.vro.service.spi.db;

import gov.va.vro.service.spi.model.AssessmentResult;
import gov.va.vro.service.spi.model.Claim;

import java.util.UUID;

public interface SaveToDbService {
  Claim insertClaim(Claim claim);

  AssessmentResult insertAssessmentResult(
      UUID id, String assessmentResult, String veteranIcn, String diagnosticCode)
      throws NoSuchFieldException;
}
