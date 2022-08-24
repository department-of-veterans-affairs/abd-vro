package gov.va.vro.api.model;

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
  @Schema(example = "Joe")
  private String first;

  @Schema(example = "M")
  private String middle;

  @Schema(example = "Doe")
  private String last;

  @Schema(example = "Jr")
  private String suffix;

  @Schema(example = "01/04/1971")
  private String birthdate;
}
