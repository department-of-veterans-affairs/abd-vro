package gov.va.vro.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
