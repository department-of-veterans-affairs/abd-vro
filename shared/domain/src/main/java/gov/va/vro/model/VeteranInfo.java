package gov.va.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VeteranInfo {
  @Schema(description = "Veteran first name", example = "Joe")
  private String first;

  @Schema(description = "Veteran middle initial", example = "M")
  private String middle;

  @Schema(description = "Veteran last name", example = "Doe")
  private String last;

  @Schema(description = "Veteran name suffix", example = "Jr")
  private String suffix;

  @Schema(description = "Veteran date of birth", example = "01/04/1971")
  private String birthdate;
}
