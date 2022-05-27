package gov.va.vro.service.provider.processors;

import gov.va.starter.example.service.spi.claimsubmission.ClaimSubmissionService;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.persistence.model.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ClaimProcessorA {

  @Autowired ClaimSubmissionService claimSubmissionService;

  public Payload process(ClaimSubmission claim) {
    HashMap<String, Object> results = new HashMap<String, Object>();
    results.put("bp_systolic", 120);
    results.put("bp_diastolic", 80);
    results.put("vro_pdf_path", "gov/va/vro/hypertension/" + claim.getSubmissionId() + ".pdf");

    claimSubmissionService.updateStatusById(claim.getId(), ClaimSubmission.ClaimStatus.DONE_VRO);
    return new Payload(claim.getSubmissionId(), "Success", results);
  }
}
