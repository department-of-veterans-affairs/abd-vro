package gov.va.vro.service.spi.db;

import gov.va.vro.service.spi.model.AssessmentResult;
import gov.va.vro.service.spi.model.Claim;

public interface SaveToDbService {
  Claim insertClaim(Claim claim);

  AssessmentResult insertAssessmentResult(AssessmentResult ar);
}
