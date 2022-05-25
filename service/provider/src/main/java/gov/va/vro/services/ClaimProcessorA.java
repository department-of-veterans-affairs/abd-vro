package gov.va.vro.services;

import gov.va.starter.example.persistence.model.ClaimSubmissionEntity;
import gov.va.vro.model.ClaimStatus;
import gov.va.vro.model.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ClaimProcessorA {

  @Autowired ClaimService claimService;

  public Payload process(ClaimSubmissionEntity claim) {
    HashMap<String, Object> results = new HashMap<String, Object>();
    results.put("bp_systolic", 120);
    results.put("bp_diastolic", 80);
    results.put("vro_pdf_path", "gov/va/vro/hypertension/" + claim.getSubmissionId() + ".pdf");

    claimService.updateStatus(claim.getId(), ClaimStatus.DONE_VRO);
    return new Payload(claim.getSubmissionId(), "Success", results);
  }
}
