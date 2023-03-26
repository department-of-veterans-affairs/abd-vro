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

  @Schema(description = "Receipt date", example = "1999-12-31")
  private String receiptDate;

  @Schema(description = "Page", example = "1")
  private String page;

  @Schema(description = "Document Identifier", example = "{BFA4943C-4F56-4AC5-B48F-5FDE469B1226}")
  private String documentId;
}
