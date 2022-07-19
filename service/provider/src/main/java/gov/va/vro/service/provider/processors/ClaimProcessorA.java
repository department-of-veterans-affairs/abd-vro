package gov.va.vro.service.provider.processors;

import gov.va.starter.example.service.spi.claimsubmission.ClaimSubmissionService;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.persistence.model.PayloadEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Deprecated // part of demo code
@Service
@RequiredArgsConstructor
public class ClaimProcessorA {

  private final ClaimSubmissionService claimSubmissionService;

  public PayloadEntity process(ClaimSubmission claim) {
    HashMap<String, Object> results = new HashMap<String, Object>();
    results.put("bp_systolic", 120);
    results.put("bp_diastolic", 80);
    results.put("vro_pdf_path", "gov/va/vro/hypertension/" + claim.getSubmissionId() + ".pdf");

    claimSubmissionService.updateStatusById(claim.getId(), ClaimSubmission.ClaimStatus.DONE_VRO);
    return new PayloadEntity(claim.getSubmissionId(), "Success", results);
  }
}
