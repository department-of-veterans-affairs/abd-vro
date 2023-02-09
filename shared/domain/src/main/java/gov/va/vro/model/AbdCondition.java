package gov.va.vro.model;

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
public class AbdCondition implements Comparable<AbdCondition> {
  @EqualsAndHashCode.Include private String text;
  @EqualsAndHashCode.Include private String code;
  private String abatementDate;
  @EqualsAndHashCode.Include private String status;
  @EqualsAndHashCode.Include private String onsetDate;
  private String recordedDate;
  private String relevant;
  private String category;

  @Schema(description = "Formatted date", example = "01/01/2023")
  private String dateFormatted;

  @Schema(description = "Source of this data", example = "LH")
  private String dataSource = "LH";

  @Schema(description = "Partial date from OCR", example = "**/**/1988")
  private String partialDate;

  @Override
  public int compareTo(AbdCondition otherCondition) {
    return StringUtils.compare(onsetDate, otherCondition.onsetDate);
  }
}
