package gov.va.starter.example.service.spi.db;

import gov.va.starter.example.service.spi.db.model.Claim;

public interface SaveToDbService {
  void persistClaim(Claim claim);
}
