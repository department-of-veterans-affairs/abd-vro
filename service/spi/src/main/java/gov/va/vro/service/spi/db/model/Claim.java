package gov.va.vro.service.spi.db.model;

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

  private UUID recordId;

  @NotNull private String claimId;

  // At the moment, this is the only id type
  @Builder.Default @NotNull private String idType = "va.gov-Form526Submission";

  @Builder.Default @NotNull private String incomingStatus = "submission";

  @NotNull private String veteranIcn;

  @NotNull private String diagnosticCode;
}
