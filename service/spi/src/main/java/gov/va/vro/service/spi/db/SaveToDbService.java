package gov.va.vro.service.spi.db;

import gov.va.vro.service.spi.db.model.Claim;

public interface SaveToDbService {
  Claim insertClaim(Claim claim);
}
