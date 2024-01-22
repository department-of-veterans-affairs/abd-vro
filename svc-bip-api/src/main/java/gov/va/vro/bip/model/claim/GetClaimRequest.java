package gov.va.vro.bip.model.claim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.bip.model.BipPayloadRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetClaimRequest implements BipPayloadRequest {
  @JsonProperty("claimId")
  private long claimId;
}
