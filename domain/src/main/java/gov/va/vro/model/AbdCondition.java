package gov.va.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdCondition implements Comparable<AbdCondition> {
  private String text;
  private String code;
  private String abatementDate;
  private String status;
  private String onsetDate;

  @Override
  public int compareTo(AbdCondition otherCondition) {
    return StringUtils.compare(onsetDate, otherCondition.onsetDate);
  }
}
