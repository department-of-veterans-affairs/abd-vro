package gov.va.vro.api.responses;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import gov.va.vro.api.model.ClaimInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(includeFieldNames = true)
public class ClaimInfoResponse {
  @JsonUnwrapped ClaimInfo claim;
}
