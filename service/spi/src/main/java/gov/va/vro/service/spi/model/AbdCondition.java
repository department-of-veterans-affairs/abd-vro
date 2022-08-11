package gov.va.vro.service.spi.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
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
