package gov.va.vro.services.bie.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class BieMessagePayload {

  private String topic;

  private String notifiedAt;

  private String event;
}
