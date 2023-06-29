package gov.va.vro.services.bie.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@JsonInclude
@Setter
@Getter
@Builder
@Jacksonized
@ToString
public class BieMessagePayload {

  @JsonProperty("topic")
  private String topic;

  @JsonProperty("notifiedAt")
  private String notifiedAt;

  @JsonProperty("event")
  private String event;
}
