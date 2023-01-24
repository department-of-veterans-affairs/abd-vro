package gov.va.vro.model.mas.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.model.mas.ClaimDetail;
import gov.va.vro.model.mas.VeteranIdentifiers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Schema(name = "MASClaimDetailsRequest", description = "Initiate a MAS request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasAutomatedClaimRequest {

  @Schema(hidden = true)
  @Setter
  @Getter
  private String correlationId;

  @NotBlank(message = "Date of Birth cannot be empty")
  @Schema(description = "Veteran Date of Birth", example = "2000-02-19")
  @JsonProperty("dob")
  private String dateOfBirth;

  @NotBlank(message = "First Name cannot be empty")
  @Schema(description = "Veteran First  Name", example = "Rick")
  private String firstName;

  @NotBlank(message = "Last Name cannot be empty")
  @Schema(description = "Veteran Last  Name", example = "Smith")
  private String lastName;

  @Schema(description = "Veteran Gender")
  private String gender;

  @NotNull(message = "Collection ID cannot be empty")
  @Schema(description = "Collection ID", example = "350")
  private Integer collectionId;

  @NotNull
  @Valid
  @Schema(description = "Veteran Identifiers")
  private VeteranIdentifiers veteranIdentifiers;

  @NotNull
  @Valid
  @Schema(description = "Details of the Claim")
  private ClaimDetail claimDetail;

  @Schema(description = "Veteran Flash Ids")
  private List<String> veteranFlashIds;

  @Schema(description = "Documents unavailable for automated review")
  private List<String> documentsWithoutAnnotationsChecked;
}
