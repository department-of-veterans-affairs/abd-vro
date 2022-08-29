package gov.va.vro.service.spi.services.fetchclaims;

import gov.va.vro.service.spi.model.Claim;

import java.util.List;

public interface FetchClaimsService {
  List<Claim> fetchClaims();
}
