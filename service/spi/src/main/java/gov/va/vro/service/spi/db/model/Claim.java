package gov.va.vro.service.spi.db.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Claim {

  @NotNull private String claimSubmissionId;

  // At the moment, this is the only id type
  @NotNull private String idType = "va.gov-Form526Submission";

  @NotNull private String incomingStatus = "submission";

  @NotNull private String veteranIcn;

  @NotNull private String diagnosticCode;
}
