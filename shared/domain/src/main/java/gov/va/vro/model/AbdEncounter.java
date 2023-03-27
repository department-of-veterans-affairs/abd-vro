package gov.va.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdEncounter {
  @EqualsAndHashCode.Include private String text;
  @EqualsAndHashCode.Include private String code;
  @EqualsAndHashCode.Include private String serviceProvider;
  @EqualsAndHashCode.Include private String location;
  @EqualsAndHashCode.Include private String reasonCode;
  @EqualsAndHashCode.Include private String status;
  @EqualsAndHashCode.Include private String periodEnd;
  @EqualsAndHashCode.Include private String encounterClass;

  @Schema(description = "Source of this data", example = "LH")
  private String dataSource = "LH";
}
