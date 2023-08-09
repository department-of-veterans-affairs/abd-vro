package gov.va.vro.model.biekafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Setter
@Getter
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BieMessagePayload {
  private Integer status;
  private String statusMessage;
  private ContentionEvent eventType;
  private long claimId;
  private long contentionId;
  private String contentionTypeCode;
  private String contentionClassificationName;
  private String diagnosticTypeCode;
  private Map<String, Object> eventDetails;
  private Long notifiedAt;
  private Long occurredAt;
}
