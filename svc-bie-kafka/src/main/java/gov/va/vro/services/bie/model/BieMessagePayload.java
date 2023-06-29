package gov.va.vro.services.bie.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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

  private String topic;

  private String notifiedAt;

  private String event;
}
