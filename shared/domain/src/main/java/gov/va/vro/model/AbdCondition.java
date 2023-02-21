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

  @Schema(
      description = "Location where the measurement taken",
      example = "WASHINGTON VA MEDICAL CENTER")
  private String organization;

  @Schema(description = "Document Identifier", example = "{BFA4943C-4F56-4AC5-B48F-5FDE469B1226}")
  private String document;

  @Schema(description = "VBMS Receipt Date", example = "04/05/2021")
  private String receiptDate;

  @Schema(description = "Document Page Number", example = "55")
  private String page;

  @Override
  public int compareTo(AbdCondition otherCondition) {
    return StringUtils.compare(onsetDate, otherCondition.onsetDate);
  }
}
