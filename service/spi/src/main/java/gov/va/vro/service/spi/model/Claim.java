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

  public static final String DEFAULT_ID_TYPE = "va.gov-Form526Submission";

  private UUID recordId;

  // Either benefitClaimId or CollectionId must be filled out
  // CollectionId is equivalent to reference_id on the claim submission table
  private String benefitClaimId;

  private String collectionId;

  // At the moment, this is the only id type
  @Builder.Default @NotNull private String idType = DEFAULT_ID_TYPE;

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
