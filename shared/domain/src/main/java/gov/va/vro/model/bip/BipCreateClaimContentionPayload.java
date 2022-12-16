package gov.va.vro.model.bip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** @author warren @Date 11/16/22 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipCreateClaimContentionPayload {
  @JsonProperty("claimId")
  private long claimId;

  @JsonProperty("contention")
  private CreateContention contention;
}
