package gov.va.vro.bip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BipPayloadRequest {
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String externalUserId;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String externalKey;
}
