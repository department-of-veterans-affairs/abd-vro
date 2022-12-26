package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.vro.model.event.Auditable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Schema(name = "MASClaimDetailsRequest", description = "Initiate a MAS request")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasAutomatedClaimPayload implements Auditable {

  public static final String BLOOD_PRESSURE_DIAGNOSTIC_CODE = "7101";
  public static final String DISABILITY_ACTION_TYPE_NEW = "NEW";

  public static final String DISABILITY_ACTION_TYPE_INCREASE = "INCREASE";

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

  /**
   * Get diagnostic code.
   *
   * @return code.
   */
  @JsonIgnore
  public String getDiagnosticCode() {
    if (claimDetail == null || claimDetail.getConditions() == null) {
      return null;
    }
    return claimDetail.getConditions().getDiagnosticCode();
  }

  /**
   * Get disability action type.
   *
   * @return type.
   */
  @JsonIgnore
  public String getDisabilityActionType() {
    if (claimDetail == null || claimDetail.getConditions() == null) {
      return null;
    }
    return claimDetail.getConditions().getDisabilityActionType();
  }

  /**
   * Check if it is in scope.
   *
   * @return true or false.
   */
  @JsonIgnore
  public boolean isInScope() {
    return Objects.equals(getDiagnosticCode(), BLOOD_PRESSURE_DIAGNOSTIC_CODE)
        && (Objects.equals(getDisabilityActionType(), DISABILITY_ACTION_TYPE_NEW)
            || Objects.equals(getDisabilityActionType(), DISABILITY_ACTION_TYPE_INCREASE));
  }

  @JsonIgnore
  public Integer getClaimId() {
    return claimDetail == null ? null : Integer.parseInt(claimDetail.getBenefitClaimId());
  }

  @JsonIgnore
  public String getVeteranIcn() {
    return veteranIdentifiers == null ? null : veteranIdentifiers.getIcn();
  }

  @Override
  @JsonIgnore
  public String getEventId() {
    return correlationId;
  }

  @JsonIgnore
  @Override
  public String getDetails() {
    return String.format(
        "collectionId = %d, claimId = %d, veteranIcn = %s, diagnosticCode = %s",
        collectionId, getClaimId(), getVeteranIcn(), getDiagnosticCode());
  }
}
