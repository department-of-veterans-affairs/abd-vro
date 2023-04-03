package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.va.vro.model.event.Auditable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
  public static final String CLAIM_V2_ID_TYPE = "mas-Form526Submission";

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

  @Builder.Default @NotNull private String idType = CLAIM_V2_ID_TYPE;

  @Setter private String offRampReason;
  // @Setter private String offRampError;

  @Setter private UUID evidenceSummaryDocumentId;

  private List<String> veteranFlashIds;

  @JsonIgnore
  public String getConditionName() {
    if (claimDetail == null || claimDetail.getConditions() == null) {
      return null;
    }
    return claimDetail.getConditions().getName();
  }

  @JsonIgnore
  public String getDiagnosticCode() {
    if (claimDetail == null || claimDetail.getConditions() == null) {
      return null;
    }
    return claimDetail.getConditions().getDiagnosticCode();
  }

  @JsonIgnore
  public String getDisabilityClassificationCode() {
    if (claimDetail == null || claimDetail.getConditions() == null) {
      return null;
    }
    return claimDetail.getConditions().getDisabilityClassificationCode();
  }

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
  public Boolean isPresumptive() {
    if (Objects.equals(getDisabilityActionType(), DISABILITY_ACTION_TYPE_NEW)) {
      return (veteranFlashIds != null
          && !Collections.disjoint(
              veteranFlashIds,
              Arrays.asList(MasVeteranFlashProps.getInstance().getAgentOrangeFlashIds())));
    }
    return null;
  }

  @JsonIgnore
  public String getBenefitClaimId() {
    return claimDetail == null ? null : claimDetail.getBenefitClaimId();
  }

  @JsonIgnore
  public String getVeteranIcn() {
    return veteranIdentifiers == null ? null : veteranIdentifiers.getIcn();
  }

  @JsonIgnore
  public String getVeteranParticipantId() {
    return veteranIdentifiers == null ? null : veteranIdentifiers.getParticipantId();
  }

  @JsonIgnore
  @Override
  public String getEventId() {
    return correlationId;
  }

  @Override
  @SneakyThrows
  @JsonIgnore
  public Map<String, String> getDetails() {
    Map<String, String> detailsMap = new HashMap<>();
    detailsMap.put("benefitClaimId", getBenefitClaimId());
    detailsMap.put("collectionId", Objects.toString(getCollectionId()));
    detailsMap.put("conditionName", getConditionName());
    detailsMap.put("diagnosticCode", getDiagnosticCode());
    detailsMap.put("veteranIcn", getVeteranIcn());
    detailsMap.put("disabilityActionType", getDisabilityActionType());
    detailsMap.put("disabilityClassificationCode", getDisabilityClassificationCode());
    detailsMap.put(
        "flashIds", getVeteranFlashIds() == null ? null : Objects.toString(getVeteranFlashIds()));
    detailsMap.put("inScope", Objects.toString(isInScope()));
    detailsMap.put("presumptive", Objects.toString(isPresumptive()));
    detailsMap.put(
        "submissionSource", claimDetail == null ? null : claimDetail.getClaimSubmissionSource());
    detailsMap.put(
        "submissionDate",
        claimDetail == null ? null : Objects.toString(claimDetail.getClaimSubmissionDateTime()));
    detailsMap.put("offRampReason", getOffRampReason());
    return detailsMap;
  }

  @JsonIgnore
  @Override
  public String getDisplayName() {
    return "Automated Claim";
  }
}
