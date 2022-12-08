package gov.va.vro.controller;

import gov.va.vro.api.model.ClaimInfo;
import gov.va.vro.api.resources.DevResource;
import gov.va.vro.controller.mapper.PostClaimRequestMapper;
import gov.va.vro.model.HealthDataAssessment;
import gov.va.vro.service.provider.CamelEntrance;
import gov.va.vro.service.spi.model.Claim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Profile("!qa & !sandbox & !prod")
public class DevController implements DevResource {


  private final CamelEntrance camelEntrance;
  private final PostClaimRequestMapper postClaimRequestMapper;

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

  private ClaimInfo getClaimInfo(Claim claim) {
    ClaimInfo info = new ClaimInfo();
    info.setClaimSubmissionId(claim.getClaimSubmissionId());
    info.setVeteranIcn(claim.getVeteranIcn());
    info.setContentions(claim.getContentions().stream().toList());
    return info;
  }
}
