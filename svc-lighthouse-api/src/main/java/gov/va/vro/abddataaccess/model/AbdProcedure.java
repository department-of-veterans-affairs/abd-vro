package gov.va.vro.abddataaccess.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdProcedure implements Comparable<AbdProcedure> {
  @EqualsAndHashCode.Include private String text;
  @EqualsAndHashCode.Include private String code;
  @EqualsAndHashCode.Include private String status;
  @EqualsAndHashCode.Include private String performedDate;
  @EqualsAndHashCode.Include private String codeSystem;

  @Schema(description = "Source of this data", example = "LH")
  private String dataSource = "LH";

  @Override
  public int compareTo(AbdProcedure otherProcedure) {
    return StringUtils.compare(performedDate, otherProcedure.performedDate);
  }
}
