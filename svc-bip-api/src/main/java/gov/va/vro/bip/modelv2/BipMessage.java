package gov.va.vro.bip.modelv2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BipMessage {
  @JsonProperty("key")
  private final String key;

  @JsonProperty("severity")
  private final String severity;

  @JsonProperty("status")
  private final int status;

  @JsonProperty("httpStatus")
  private final String httpStatus;

  @JsonProperty("text")
  private final String text;

  @JsonProperty("timestamp")
  private final String timestamp;
}
