package gov.va.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceLocation {

  @Schema(description = "Veteran service location", example = "Vietnam")
  private String location;

  @Schema(
      description = "Source document of information",
      example = "VA 21-3101 Request for Information")
  private String document;

  private String receiptDate;
  private String page;
  private String documentId;
}
