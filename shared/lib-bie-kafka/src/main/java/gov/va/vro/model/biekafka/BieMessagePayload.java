package gov.va.vro.model.biekafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BieMessagePayload {
  private Integer status;
  private String statusMessage;
  private String event;
  private String eventDetails;
  private String notifiedAt;
}
