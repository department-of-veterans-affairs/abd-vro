package gov.va.vro.model.rrd.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MAS order exam conditions.
 *
 * @author warren @Date 10/11/22
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasOrderExamConditions {
  @JsonProperty("contentionText")
  private String contentionText;

  @JsonProperty("conditionCode")
  private String conditionCode;
}
