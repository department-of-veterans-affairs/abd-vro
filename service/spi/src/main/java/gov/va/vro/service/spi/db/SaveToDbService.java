package gov.va.vro.service.spi.db;

import gov.va.vro.service.spi.model.Claim;

import java.util.UUID;

public interface SaveToDbService {
  Claim insertClaim(Claim claim);

  void insertAssessmentResult(UUID claimId, String evidence, String diagnosticCode)
      throws NoSuchFieldException;
}
