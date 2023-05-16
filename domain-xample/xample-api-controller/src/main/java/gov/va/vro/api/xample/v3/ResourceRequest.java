package gov.va.vro.api.xample.v3;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(name = "ResourceRequest", description = "Details for ResourceRequest")
public class ResourceRequest {
  @NotBlank
  @Schema(description = "resource ID", example = "1234")
  private String resourceId;

  @NotBlank
  @Schema(description = "Diagnostic code", example = "A")
  private String diagnosticCode;
}
