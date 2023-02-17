package gov.va.vro.service.spi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor_ = {@JsonIgnore})
@EqualsAndHashCode
@Builder
@ToString
public class Claim {

  public static final String V1_ID_TYPE = "va.gov-Form526Submission";

  private UUID recordId;

  // We should either get a benefitClaimId from MAS for v2 endpoints, or v1 endpoints provide a
  // claimSubmissionId that we use to set the collectionId
  private String benefitClaimId;

  private String collectionId;

  // For backwards compatibility with v1 routes. On the way in, mappers set collectionId to the
  // payload's claimSubmissionId.
  // Both ways tie to the reference_id on claim submission table.
  public String getClaimSubmissionId() {
    return collectionId;
  }

  // At the moment, this is the only id type
  @Builder.Default @NotNull private String idType = V1_ID_TYPE;

  @Builder.Default @NotNull private String incomingStatus = "submission";

  @NotNull private String veteranIcn;

  @NotNull private String diagnosticCode;

  private Set<String> contentions;

  private String offRampReason;

  private boolean presumptiveFlag;

  private String disabilityActionType;

  private boolean inScope;

  private String submissionSource;

  private OffsetDateTime submissionDate;

  private String claimSubmissionDateTime;
}
