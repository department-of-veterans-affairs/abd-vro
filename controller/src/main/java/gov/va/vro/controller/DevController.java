package gov.va.vro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.starter.boot.exception.RequestValidationException;
import gov.va.vro.api.model.ClaimInfo;
import gov.va.vro.api.model.ClaimProcessingException;
import gov.va.vro.api.requests.HealthDataAssessmentRequest;
import gov.va.vro.api.resources.DevResource;
import gov.va.vro.api.responses.FetchClaimsResponse;
import gov.va.vro.controller.mapper.PostClaimRequestMapper;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.services.FetchClaimsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Profile("!qa & !sandbox & !prod")
public class DevController implements DevResource {

  private final CamelEntrance camelEntrance;
  private final PostClaimRequestMapper postClaimRequestMapper;

  private final FetchClaimsService fetchClaimsService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public ResponseEntity<HealthDataAssessment> postHealthAssessment(
      HealthDataAssessmentRequest claim)
      throws RequestValidationException, ClaimProcessingException {
    log.info(
        "Getting health assessment for claim {} and veteran icn {}",
        claim.getClaimSubmissionId(),
        claim.getVeteranIcn());
    try {
      Claim model = postClaimRequestMapper.toModel(claim);
      String responseAsString = camelEntrance.submitClaim(model);

      HealthDataAssessment response =
          objectMapper.readValue(responseAsString, HealthDataAssessment.class);
      if (response.getEvidence() == null) {
        throw new ClaimProcessingException(
            claim.getClaimSubmissionId(), HttpStatus.NOT_FOUND, "No evidence found.");
      }
      log.info("Returning health assessment for: {}", response.getVeteranIcn());
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (ClaimProcessingException cpe) {
      throw cpe;
    } catch (Exception ex) {
      log.error("Error in health assessment", ex);
      throw new ClaimProcessingException(
          claim.getClaimSubmissionId(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
  }

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
