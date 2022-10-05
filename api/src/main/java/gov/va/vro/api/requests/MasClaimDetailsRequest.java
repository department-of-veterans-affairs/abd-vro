package gov.va.vro.api.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.api.model.mas.ClaimDetail;
import gov.va.vro.api.model.mas.VeteranIdentifiers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Schema(name = "MASClaimDetailsRequest", description = "Initiate a MAS request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasClaimDetailsRequest {

  @NotBlank(message = "Date of Birth cannot be empty")
  @Schema(description = "Veteran Date of Birth", example = "2000-02-19")
  private String dob;

  @NotBlank(message = "First Name cannot be empty")
  @Schema(description = "Veteran First  Name", example = "Rick")
  @JsonProperty("firstname")
  private String firstName;

  @NotBlank(message = "Last Name cannot be empty")
  @Schema(description = "Veteran Last  Name", example = "Smith")
  @JsonProperty("lastname")
  private String lastName;

  @Schema(description = "Veteran Gender")
  private String gender;

  @NotBlank(message = "Collections ID empty")
  @Schema(description = "Collections ID")
  @JsonProperty("collectionsid")
  private String collectionsId;

  @NotNull
  @Valid
  @Schema(description = "Veteran Identifiers")
  @JsonProperty("veteranidentifiers")
  private VeteranIdentifiers veteranIdentifiers;

  @NotNull
  @Valid
  @Schema(description = "Details of the Claim")
  @JsonProperty("claimdetail")
  private ClaimDetail claimDetail;
}
