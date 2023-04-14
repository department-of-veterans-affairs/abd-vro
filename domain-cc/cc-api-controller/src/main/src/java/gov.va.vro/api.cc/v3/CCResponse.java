package gov.va.vro.api.cc.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CCResponse {

  @NotBlank private String resourceId;
  private String diagnosticCode;
  private String status;
  private int statusCode;
  private String statusMessage;
}
