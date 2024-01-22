package gov.va.vro.bip.model.lifecycle;

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
public class PutClaimLifecycleRequest implements BipPayloadRequest {
  @JsonProperty("claimId")
  long claimId;

  @JsonProperty("claimLifecycleStatus")
  String claimLifecycleStatus;
}
