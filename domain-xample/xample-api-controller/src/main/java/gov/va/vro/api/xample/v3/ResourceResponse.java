package gov.va.vro.api.xample.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResourceResponse {

  @NotBlank private String resourceId;
  private String diagnosticCode;
  private String status;
  private int statusCode;
  private String statusMessage;
}
