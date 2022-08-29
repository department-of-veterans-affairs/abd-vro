package gov.va.vro.service.spi.services.fetchclaims;

import gov.va.vro.service.spi.model.SimpleClaim;

import java.util.List;

public interface FetchClaimsService {
  List<SimpleClaim> fetchClaims();
}
