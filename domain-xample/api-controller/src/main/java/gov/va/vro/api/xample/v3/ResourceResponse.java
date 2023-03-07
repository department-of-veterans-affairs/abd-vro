package gov.va.vro.api.xample.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResourceResponse {

  @NotBlank private String resourceId;
  private String diagnosticCode;
  private String status;
  private String reason;
}
