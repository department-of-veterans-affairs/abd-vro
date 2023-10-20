package gov.va.vro.model.rrd.mas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GeneratePdfResponse {

  @NotBlank private String claimSubmissionId;
  private String status;
  private String reason;
}
