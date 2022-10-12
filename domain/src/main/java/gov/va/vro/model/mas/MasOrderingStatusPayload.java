package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Schema(name = "MASExamOrderingStatusRequest", description = "Initiate a MAS request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasOrderingStatusPayload {

  @NotBlank(message = "Collection ID cannot be empty")
  @Schema(description = "Collection ID", example = "999")
  private String collectionId;

  @Schema(description = "Collection Status", example = "DRAFT")
  private String collectionStatus;

  @Schema(
      description = "Exam order timestamp",
      example =
          "225-63-70T34:63:40.566614908642876440526533774826377332727118627060130905913+84:54")
  private String examOrderDateTime;
}
