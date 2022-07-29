package gov.va.vro.api.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Setter
public class AbdMedication implements Comparable<AbdMedication> {
  private String status;
  private List<String> notes;
  private String description;
  private int refills;
  private String duration;
  private String authoredOn;
  private List<String> dosageInstruction;
  private String route;

  @Override
  public int compareTo(AbdMedication otherMedication) {
    return StringUtils.compare(authoredOn, otherMedication.authoredOn);
  }
}
