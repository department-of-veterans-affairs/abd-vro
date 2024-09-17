package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BipPayloadRequest {
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String externalUserId;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String externalKey;
}
