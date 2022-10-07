package gov.va.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdMedication implements Comparable<AbdMedication> {
  private String status;
  private List<String> notes;
  private String description;
  private int refills;
  private Boolean asthmaRelevant;
  private String duration;

  @Schema(example = "1950-04-05T23:00:00Z")
  private String authoredOn;

  private List<String> dosageInstructions;
  private String route;

  @Override
  public int compareTo(AbdMedication otherMedication) {
    return StringUtils.compare(authoredOn, otherMedication.authoredOn);
  }
}
