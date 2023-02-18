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
public class AbdBloodPressure implements Comparable<AbdBloodPressure> {
  @EqualsAndHashCode.Include
  @Schema(description = "The date blood pressure is taken", example = "2020-08-05")
  private String date;

  @EqualsAndHashCode.Include
  @Schema(description = "Diastolic measurement")
  private AbdBpMeasurement diastolic;

  @EqualsAndHashCode.Include
  @Schema(description = "Systolic measurement")
  private AbdBpMeasurement systolic;

  @Schema(
      description = "Name of the physician who took the measurement",
      example = "DR. THOMAS REYNOLDS PHD")
  private String practitioner;

  @EqualsAndHashCode.Include
  @Schema(
      description = "Location where the measurement taken",
      example = "WASHINGTON VA MEDICAL CENTER")
  private String organization;

  @Schema(
          description = "Document Type Description",
          example = "Medical Treatment Record - Government Facility")
  private String document;

  @Schema(description = "VBMS Receipt Date", example = "04/05/2021")
  private String receiptDate;

  @Schema(description = "Document Page Number", example = "55")
  private String page;

  @Schema(description = "Document Identifier", example = "{BFA4943C-4F56-4AC5-B48F-5FDE469B1226}")
  private String documentId;

  @Schema(description = "Formatted date", example = "01/01/2023")
  private String dateFormatted;

  @Schema(description = "Source of this data", example = "LH")
  private String dataSource = "LH";

  @Schema(description = "Partial date from OCR", example = "**/**/1988")
  private String partialDate;

  @Override
  public int compareTo(AbdBloodPressure otherBp) {
    return StringUtils.compare(date, otherBp.date);
  }
}
