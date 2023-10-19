package gov.va.vro.model.rrd.mas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.model.rrd.event.Auditable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Builder
@Getter
@Schema(name = "MASExamOrderingStatusRequest", description = "Initiate a MAS request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasExamOrderStatusPayload implements Auditable {

  @JsonIgnore @Setter private String correlationId;

  @NotNull(message = "Collection ID is required")
  @Schema(description = "Collection ID", example = "999")
  private Integer collectionId;

  @NotBlank(message = "Collection Status is required")
  @Schema(description = "Collection Status", example = "DRAFT")
  private String collectionStatus;

  @Schema(description = "Exam order timestamp", example = "2018-11-04T17:45:59Z")
  private String examOrderDateTime;

  @JsonIgnore
  @Override
  public String getEventId() {
    return correlationId;
  }

  @JsonIgnore
  @Override
  public Map<String, String> getDetails() {
    Map<String, String> detailsMap = new HashMap<>();
    detailsMap.put("collectionId", Integer.toString(collectionId));
    detailsMap.put("collectionStatus", collectionStatus);
    detailsMap.put("examOrderDateTime", examOrderDateTime);
    return detailsMap;
  }

  @Override
  public String getDisplayName() {
    return "Exam Order Status";
  }
}
