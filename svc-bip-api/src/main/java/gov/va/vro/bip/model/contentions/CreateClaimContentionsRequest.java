package gov.va.vro.bip.model.contentions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.bip.model.BipPayloadRequest;
import gov.va.vro.bip.model.Contention;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateClaimContentionsRequest implements BipPayloadRequest {
  @JsonProperty("claimId")
  private long claimId;

  @JsonProperty("createContentions")
  private List<Contention> createContentions;
}
