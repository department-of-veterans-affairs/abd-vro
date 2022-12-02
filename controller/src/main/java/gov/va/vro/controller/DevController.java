package gov.va.vro.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import gov.va.vro.api.model.ClaimInfo;
import gov.va.vro.api.resources.DevResource;
import gov.va.vro.api.responses.FetchClaimsResponse;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.services.FetchClaimsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Profile("!qa & !sandbox & !prod")
public class DevController implements DevResource {

  private final FetchClaimsService fetchClaimsService;

  @Override
  public ResponseEntity<FetchClaimsResponse> fetchClaims() {

    try {

      List<Claim> claimList = fetchClaimsService.fetchClaims();
      List<ClaimInfo> claims =
          claimList.stream().map(this::getClaimInfo).collect(Collectors.toList());
      FetchClaimsResponse response = new FetchClaimsResponse(claims, "Success");
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      FetchClaimsResponse failure = new FetchClaimsResponse();
      failure.setErrorMessage("Could not fetch claims from the DB.  " + e.getCause());
      log.error("Could not fetch claims from the DB.", e);
      return new ResponseEntity<>(failure, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private ClaimInfo getClaimInfo(Claim claim) {
    ClaimInfo info = new ClaimInfo();
    info.setClaimSubmissionId(claim.getClaimSubmissionId());
    info.setVeteranIcn(claim.getVeteranIcn());
    info.setContentions(claim.getContentions().stream().toList());
    return info;
  }
}
