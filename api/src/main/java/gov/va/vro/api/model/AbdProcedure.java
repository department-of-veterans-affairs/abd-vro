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
