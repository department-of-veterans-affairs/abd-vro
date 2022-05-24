package gov.va.vro.services;

import gov.va.vro.model.Claim;
import gov.va.vro.model.ClaimStatus;
import gov.va.vro.model.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ClaimProcessorA {

  @Autowired ClaimService claimService;

  public Claim claimFactory() {
    return Claim.builder().submission_id("subm1").claimant_id("vet2").build();
  }

  public Payload process(Claim claim) {
    HashMap<String, Object> results = new HashMap<String, Object>();
    results.put("bp_systolic", 120);
    results.put("bp_diastolic", 80);
    results.put("vro_pdf_path", "gov/va/vro/hypertension/" + claim.getSubmission_id() + ".pdf");

    claimService.updateStatus(claim.getUuid(), ClaimStatus.DONE_VRO);
    return new Payload(claim.getSubmission_id(), "Success", results);
  }
}
