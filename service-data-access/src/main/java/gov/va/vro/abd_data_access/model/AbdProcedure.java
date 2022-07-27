package gov.va.vro.abd_data_access.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class AbdProcedure implements Comparable<AbdProcedure> {
  private String text;
  private String code;
  private String status;
  private String performedDate;
  private String codeSystem;

  @Override
  public int compareTo(AbdProcedure otherProcedure) {
    return StringUtils.compare(performedDate, otherProcedure.performedDate);
  }
}
