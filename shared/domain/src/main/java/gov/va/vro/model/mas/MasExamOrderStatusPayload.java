package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Schema(name = "MASExamOrderingStatusRequest", description = "Initiate a MAS request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasExamOrderStatusPayload {

  @NotNull(message = "Collection ID cannot be empty")
  @Schema(description = "Collection ID", example = "999")
  private int collectionId;

  @NotBlank(message = "Colletion Status is required")
  @Schema(description = "Collection Status", example = "DRAFT")
  private String collectionStatus;

  @Schema(description = "Exam order timestamp", example = "2018-11-04T17:45:61Z")
  private String examOrderDateTime;
}
