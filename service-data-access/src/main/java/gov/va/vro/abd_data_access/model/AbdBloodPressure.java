package gov.va.vro.abd_data_access.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AbdBloodPressure implements Comparable<AbdBloodPressure> {
  private String date;
  private AbdBPMeasurement diastolic;
  private AbdBPMeasurement systolic;
  private String practitioner;
  private String organization;

  @Override
  public int compareTo(AbdBloodPressure otherBp) {
    return StringUtils.compare(date, otherBp.date);
  }
}
