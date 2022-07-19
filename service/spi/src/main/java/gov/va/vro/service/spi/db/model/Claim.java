package gov.va.vro.service.spi.db.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class Claim {

  @NotNull private String claimSubmissionId;

  // At the moment, this is the only id type
  @Builder.Default @NotNull private String idType = "va.gov-Form526Submission";

  @Builder.Default @NotNull private String incomingStatus = "submission";

  @NotNull private String veteranIcn;

  @NotNull private String diagnosticCode;
}
