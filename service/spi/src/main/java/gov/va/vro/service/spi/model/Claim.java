package gov.va.vro.service.spi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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

  @NotNull private String claimSubmissionId;

  // At the moment, this is the only id type
  @Builder.Default @NotNull private String idType = DEFAULT_ID_TYPE;

  @Builder.Default @NotNull private String incomingStatus = "submission";

  @NotNull private String veteranIcn;

  @NotNull private String diagnosticCode;
}
