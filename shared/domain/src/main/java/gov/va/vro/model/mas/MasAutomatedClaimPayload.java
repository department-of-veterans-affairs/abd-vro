package gov.va.vro.model.mas;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.event.Auditable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder(toBuilder = true)
@Getter
public class MasAutomatedClaimPayload implements Auditable {

  public static final String BLOOD_PRESSURE_DIAGNOSTIC_CODE = "7101";
  public static final String DISABILITY_ACTION_TYPE_NEW = "NEW";
  public static final String DISABILITY_ACTION_TYPE_INCREASE = "INCREASE";
  public static final String AGENT_ORANGE_FLASH_ID = "266";

  private final ObjectMapper objectMapper = new ObjectMapper();

  private String correlationId;

  @NotBlank(message = "Date of Birth cannot be empty")
  private String dateOfBirth;

  @NotBlank(message = "First Name cannot be empty")
  private String firstName;

  @NotBlank(message = "Last Name cannot be empty")
  private String lastName;

  private String gender;

  @NotNull(message = "Collection ID cannot be empty")
  private Integer collectionId;

  @NotNull @Valid private VeteranIdentifiers veteranIdentifiers;

  @NotNull @Valid private ClaimDetail claimDetail;

  private List<String> veteranFlashIds;

  public String getDiagnosticCode() {
    if (claimDetail == null || claimDetail.getConditions() == null) {
      return null;
    }
    return claimDetail.getConditions().getDiagnosticCode();
  }

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
  public boolean isInScope() {
    return Objects.equals(getDiagnosticCode(), BLOOD_PRESSURE_DIAGNOSTIC_CODE)
        && (Objects.equals(getDisabilityActionType(), DISABILITY_ACTION_TYPE_NEW)
            || Objects.equals(getDisabilityActionType(), DISABILITY_ACTION_TYPE_INCREASE));
  }

  public Boolean isPresumptive() {
    if (Objects.equals(getDisabilityActionType(), DISABILITY_ACTION_TYPE_NEW)) {
      return (veteranFlashIds != null && veteranFlashIds.contains(AGENT_ORANGE_FLASH_ID));
    }
    return null;
  }

  public Integer getClaimId() {
    return claimDetail == null ? null : Integer.parseInt(claimDetail.getBenefitClaimId());
  }

  public String getVeteranIcn() {
    return veteranIdentifiers == null ? null : veteranIdentifiers.getIcn();
  }

  @Override
  public String getEventId() {
    return correlationId;
  }

  @Override
  @SneakyThrows
  public String getDetails() {
    var details =
        MasEventDetails.builder()
            .claimId(Objects.toString(getClaimId()))
            .collectionId(Objects.toString(getCollectionId()))
            .diagnosticCode(getDiagnosticCode())
            .veteranIcn(getVeteranIcn())
            .disabilityActionType(getDisabilityActionType())
            .flashIds(getVeteranFlashIds())
            .inScope(isInScope())
            .presumptive(isPresumptive())
            .submissionSource(claimDetail == null ? null : claimDetail.getClaimSubmissionSource())
            .submissionDate(claimDetail == null ? null : claimDetail.getClaimSubmissionDateTime())
            // TODO: .offRampReason()
            .build();
    return objectMapper.writeValueAsString(details);
  }

  @Override
  public String getDisplayName() {
    return "Automated Claim";
  }
}
