package gov.va.vro.abddataaccess.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AbdBloodPressure implements Comparable<AbdBloodPressure> {
  private String date;
  private AbdBpMeasurement diastolic;
  private AbdBpMeasurement systolic;
  private String practitioner;
  private String organization;

  @Override
  public int compareTo(AbdBloodPressure otherBp) {
    return StringUtils.compare(date, otherBp.date);
  }
}
