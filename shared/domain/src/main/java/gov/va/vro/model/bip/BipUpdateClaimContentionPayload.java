package gov.va.vro.model.bip;

/**
 * BipUpdate Claim Contention Payload
 *
 * @author warren @Date 11/16/22
 * */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipUpdateClaimContentionPayload {
  @JsonProperty("claimId")
  private long claimId;

  @JsonProperty("contention")
  private UpdateContention contention;
}
