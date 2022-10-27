package gov.va.vro.service.provider.mas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** @author warren @Date 10/11/22 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasOrderExamReq {
  @JsonProperty("conditions")
  private List<MasOrderExamConditions> conditions;

  @JsonProperty("collectionsId")
  private Integer collectionsId;
}
