package gov.va.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdMedication implements Comparable<AbdMedication> {
  @EqualsAndHashCode.Include
  @Schema(description = "Status of medication", example = "Active")
  private String status;

  @Schema(description = "Notes of the medication order")
  private List<String> notes;

  @EqualsAndHashCode.Include
  @Schema(description = "Medication name", example = "Hydrochlorothiazide 6.25 MG")
  private String description;

  @EqualsAndHashCode.Include
  @Schema(description = "Number of refills remaining", example = "2")
  private int refills;

  @EqualsAndHashCode.Include
  @Schema(description = "Medication can be for asthma", example = "true")
  private Boolean asthmaRelevant;

  private Boolean conditionRelated;
  private String suggestedCategory;

  @EqualsAndHashCode.Include
  @Schema(description = "Duration of the medication")
  private String duration;

  @EqualsAndHashCode.Include
  @Schema(description = "The date when medication ordered", example = "2021-04-05T23:00:00Z")
  private String authoredOn;

  @Schema(
      description = "Instructions on medication usage",
      example = "[\"QID PRN\", \"As directed by physician\"]")
  private List<String> dosageInstructions;

  @EqualsAndHashCode.Include
  @Schema(description = "Medication administration method", example = "INHALATION ORAL")
  private String route;

  @Override
  public int compareTo(AbdMedication otherMedication) {
    return StringUtils.compare(authoredOn, otherMedication.authoredOn);
  }
}
