package gov.va.vro.service.provider.mas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GeneratePdfResp {

  @NotBlank private String claimSubmissionId;
  private String status;
  private String reason;
}
