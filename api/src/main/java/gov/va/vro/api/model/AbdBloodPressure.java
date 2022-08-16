package gov.va.vro.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
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
