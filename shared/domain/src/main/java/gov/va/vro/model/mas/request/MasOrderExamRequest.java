package gov.va.vro.model.mas.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.model.mas.MasOrderExamConditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * MAS order exam request class.
 *
 * @author warren @Date 10/11/22
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasOrderExamRequest {
  @JsonProperty("conditions")
  private List<MasOrderExamConditions> conditions;

  @JsonProperty("collectionsId")
  private Integer collectionsId;
}
