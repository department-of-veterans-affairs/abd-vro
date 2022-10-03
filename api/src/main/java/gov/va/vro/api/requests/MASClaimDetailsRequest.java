package gov.va.vro.api.requests;

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
public class MASClaimDetailsRequest {

  @NotBlank(message = "Date of Birth cannot be empty")
  @Schema(description = "Veteran Date of Birth", example = "2000-02-19")
  private String dob;

  @NotBlank(message = "First Name cannot be empty")
  @Schema(description = "Veteran First  Name", example = "Rick")
  private String firstname;

  @NotBlank(message = "Last Name cannot be empty")
  @Schema(description = "Veteran Last  Name", example = "Smith")
  private String lastname;

  @Schema(description = "Veteran Gender")
  private String gender;

  @NotBlank(message = "Collections ID empty")
  @Schema(description = "Collections ID")
  private String collectionsid;

  @NotNull
  @Valid
  @Schema(description = "Veteran Identifiers")
  private VeteranIdentifiers veteranidentifiers;

  @NotNull
  @Valid
  @Schema(description = "Details of the Claim")
  private ClaimDetail claimdetail;
}
