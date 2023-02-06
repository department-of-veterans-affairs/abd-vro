package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/**
 * An object to provide more detailed data for a specific claim. A claim is a formal request for
 * benefits that is submitted by a Veteran, or by a person or organization authorized to act on a
 * Veteran&#39;s behalf.
 */
@Schema(
    name = "ClaimDetail",
    description =
        """
        An object to provide more detailed data for a specific claim. A claim is a formal
        request for benefits that is submitted by a Veteran, or by a person or organization
        authorized to act on a Veteran's behalf.
        """)
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ClaimDetail {

  @JsonProperty("summaryDateTime")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime summaryDateTime;

  @JsonProperty("lastModified")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastModified;

  @JsonProperty("claimId")
  private Long claimId;

  @JsonProperty("benefitClaimType")
  private BenefitClaimType benefitClaimType;

  @JsonProperty("phase")
  private PhaseType phase;

  @JsonProperty("phaseLastChangedDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime phaseLastChangedDate;

  @JsonProperty("claimLifecycleStatus")
  private String claimLifecycleStatus;

  @JsonProperty("completedDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime completedDate;

  @JsonProperty("claimant")
  private Claimant claimant;

  @JsonProperty("veteran")
  private Veteran veteran;

  @JsonProperty("receivedDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime receivedDate;

  @JsonProperty("payeeTypeCode")
  private String payeeTypeCode;

  @JsonProperty("serviceTypeCode")
  private String serviceTypeCode;

  @JsonProperty("programTypeCode")
  private String programTypeCode;

  @JsonProperty("endProductCode")
  private String endProductCode;

  @JsonProperty("tempStationOfJurisdiction")
  private String tempStationOfJurisdiction;

  @JsonProperty("claimStationOfJurisdiction")
  private String claimStationOfJurisdiction;

  @JsonProperty("awardStationOfJurisdiction")
  private String awardStationOfJurisdiction;

  @JsonProperty("suspense")
  private Suspense suspense;

  @JsonProperty("suspenseReasonCode")
  private String suspenseReasonCode;

  @JsonProperty("suspenseDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime suspenseDate;

  @JsonProperty("suspenseComment")
  private String suspenseComment;

  @JsonProperty("waiverSubmitted")
  private Boolean waiverSubmitted;

  @JsonProperty("limitedPoa")
  private LimitedPoa limitedPoa;

  @JsonProperty("establishedDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime establishedDate;

  public ClaimDetail summaryDateTime(OffsetDateTime summaryDateTime) {
    this.summaryDateTime = summaryDateTime;
    return this;
  }

  /**
   * Get summaryDateTime.
   *
   * @return summaryDateTime
   */
  @Valid
  @Schema(name = "summaryDateTime")
  public OffsetDateTime getSummaryDateTime() {
    return summaryDateTime;
  }

  public void setSummaryDateTime(OffsetDateTime summaryDateTime) {
    this.summaryDateTime = summaryDateTime;
  }

  public ClaimDetail lastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
    return this;
  }

  /**
   * Date of the last time this object was modified.
   *
   * @return lastModified
   */
  @Valid
  @Schema(name = "lastModified", description = "Date of the last time this object was modified")
  public OffsetDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public ClaimDetail claimId(Long claimId) {
    this.claimId = claimId;
    return this;
  }

  /**
   * The CorpDB BNFT_CLAIM_ID.
   *
   * @return claimId
   */
  @Schema(name = "claimId", description = "The CorpDB BNFT_CLAIM_ID")
  public Long getClaimId() {
    return claimId;
  }

  public void setClaimId(Long claimId) {
    this.claimId = claimId;
  }

  public ClaimDetail benefitClaimType(BenefitClaimType benefitClaimType) {
    this.benefitClaimType = benefitClaimType;
    return this;
  }

  /**
   * Get benefitClaimType.
   *
   * @return benefitClaimType
   */
  @Valid
  @Schema(name = "benefitClaimType")
  public BenefitClaimType getBenefitClaimType() {
    return benefitClaimType;
  }

  public void setBenefitClaimType(BenefitClaimType benefitClaimType) {
    this.benefitClaimType = benefitClaimType;
  }

  public ClaimDetail phase(PhaseType phase) {
    this.phase = phase;
    return this;
  }

  /**
   * Get phase.
   *
   * @return phase
   */
  @Valid
  @Schema(name = "phase")
  public PhaseType getPhase() {
    return phase;
  }

  public void setPhase(PhaseType phase) {
    this.phase = phase;
  }

  public ClaimDetail phaseLastChangedDate(OffsetDateTime phaseLastChangedDate) {
    this.phaseLastChangedDate = phaseLastChangedDate;
    return this;
  }

  /**
   * Get phaseLastChangedDate.
   *
   * @return phaseLastChangedDate
   */
  @Valid
  @Schema(name = "phaseLastChangedDate")
  public OffsetDateTime getPhaseLastChangedDate() {
    return phaseLastChangedDate;
  }

  public void setPhaseLastChangedDate(OffsetDateTime phaseLastChangedDate) {
    this.phaseLastChangedDate = phaseLastChangedDate;
  }

  public ClaimDetail claimLifecycleStatus(String claimLifecycleStatus) {
    this.claimLifecycleStatus = claimLifecycleStatus;
    return this;
  }

  /**
   * Also called Lifecycle status. Describes the next type of work that needs to be completed on a
   * claim. Statuses are related to the lifecycle in which the work will be completed, such as
   * Rating Correction or Pending Authorization.
   *
   * @return claimLifecycleStatus
   */
  @Schema(
      name = "claimLifecycleStatus",
      description =
          """
          Also called Lifecycle status. Describes the next type of work that needs to be
          completed on a claim. Statuses are related to the lifecycle in which the work
          will be completed, such as Rating Correction or Pending Authorization.
          """)
  public String getClaimLifecycleStatus() {
    return claimLifecycleStatus;
  }

  public void setClaimLifecycleStatus(String claimLifecycleStatus) {
    this.claimLifecycleStatus = claimLifecycleStatus;
  }

  public ClaimDetail completedDate(OffsetDateTime completedDate) {
    this.completedDate = completedDate;
    return this;
  }

  /**
   * Get completedDate.
   *
   * @return completedDate
   */
  @Valid
  @Schema(name = "completedDate")
  public OffsetDateTime getCompletedDate() {
    return completedDate;
  }

  public void setCompletedDate(OffsetDateTime completedDate) {
    this.completedDate = completedDate;
  }

  public ClaimDetail claimant(Claimant claimant) {
    this.claimant = claimant;
    return this;
  }

  /**
   * Get claimant.
   *
   * @return claimant
   */
  @Valid
  @Schema(name = "claimant")
  public Claimant getClaimant() {
    return claimant;
  }

  public void setClaimant(Claimant claimant) {
    this.claimant = claimant;
  }

  public ClaimDetail veteran(Veteran veteran) {
    this.veteran = veteran;
    return this;
  }

  /**
   * Get veteran.
   *
   * @return veteran
   */
  @Valid
  @Schema(name = "veteran")
  public Veteran getVeteran() {
    return veteran;
  }

  public void setVeteran(Veteran veteran) {
    this.veteran = veteran;
  }

  public ClaimDetail receivedDate(OffsetDateTime receivedDate) {
    this.receivedDate = receivedDate;
    return this;
  }

  /**
   * The date the claim was received.
   *
   * @return receivedDate
   */
  @Valid
  @Schema(name = "receivedDate", description = "The date the claim was received.")
  public OffsetDateTime getReceivedDate() {
    return receivedDate;
  }

  public void setReceivedDate(OffsetDateTime receivedDate) {
    this.receivedDate = receivedDate;
  }

  public ClaimDetail payeeTypeCode(String payeeTypeCode) {
    this.payeeTypeCode = payeeTypeCode;
    return this;
  }

  /**
   * Number and label that indicates the person receiving a benefit, such as Veteran or spouse.
   *
   * @return payeeTypeCode
   */
  @Schema(
      name = "payeeTypeCode",
      example = "010",
      description =
          """
          Number and label that indicates the person receiving a benefit, such as Veteran
          or spouse.
          """)
  public String getPayeeTypeCode() {
    return payeeTypeCode;
  }

  public void setPayeeTypeCode(String payeeTypeCode) {
    this.payeeTypeCode = payeeTypeCode;
  }

  public ClaimDetail serviceTypeCode(String serviceTypeCode) {
    this.serviceTypeCode = serviceTypeCode;
    return this;
  }

  /**
   * The service type code of the claim.
   *
   * @return serviceTypeCode
   */
  @Schema(
      name = "serviceTypeCode",
      example = "CP",
      description = "The service type code of the claim")
  public String getServiceTypeCode() {
    return serviceTypeCode;
  }

  public void setServiceTypeCode(String serviceTypeCode) {
    this.serviceTypeCode = serviceTypeCode;
  }

  public ClaimDetail programTypeCode(String programTypeCode) {
    this.programTypeCode = programTypeCode;
    return this;
  }

  /**
   * The program type code of the claim.
   *
   * @return programTypeCode
   */
  @Schema(
      name = "programTypeCode",
      example = "CPL",
      description = "The program type code of the claim")
  public String getProgramTypeCode() {
    return programTypeCode;
  }

  public void setProgramTypeCode(String programTypeCode) {
    this.programTypeCode = programTypeCode;
  }

  public ClaimDetail endProductCode(String endProductCode) {
    this.endProductCode = endProductCode;
    return this;
  }

  /**
   * Modified end product code (including the increment/third digit modifier). Part of the Benefits
   * Delivery Network (BDN) Master Record indicating the status conditions of the Veteran's pay
   * record, in terms of benefit payments.
   *
   * @return endProductCode
   */
  @Schema(
      name = "endProductCode",
      description =
          """
          Modified end product code (including the increment/third digit modifier). Part of
          the Benefits Delivery Network (BDN) Master Record indicating the status conditions
          of the Veteran's pay record, in terms of benefit payments.
          """)
  public String getEndProductCode() {
    return endProductCode;
  }

  public void setEndProductCode(String endProductCode) {
    this.endProductCode = endProductCode;
  }

  public ClaimDetail tempStationOfJurisdiction(String tempStationOfJurisdiction) {
    this.tempStationOfJurisdiction = tempStationOfJurisdiction;
    return this;
  }

  /**
   * Represents the Regional Office (RO) where the claim is currently being worked if different from
   * the establishing RO.
   *
   * @return tempStationOfJurisdiction
   */
  @Schema(
      name = "tempStationOfJurisdiction",
      description =
          """
          Represents the Regional Office (RO) where the claim is currently being worked if
          different from the establishing RO
          """)
  public String getTempStationOfJurisdiction() {
    return tempStationOfJurisdiction;
  }

  public void setTempStationOfJurisdiction(String tempStationOfJurisdiction) {
    this.tempStationOfJurisdiction = tempStationOfJurisdiction;
  }

  public ClaimDetail claimStationOfJurisdiction(String claimStationOfJurisdiction) {
    this.claimStationOfJurisdiction = claimStationOfJurisdiction;
    return this;
  }

  /**
   * Represents the Regional Office (RO) where the claim currently is assigned.
   *
   * @return claimStationOfJurisdiction
   */
  @Schema(
      name = "claimStationOfJurisdiction",
      description = "Represents the Regional Office (RO) where the claim currently is assigned")
  public String getClaimStationOfJurisdiction() {
    return claimStationOfJurisdiction;
  }

  public void setClaimStationOfJurisdiction(String claimStationOfJurisdiction) {
    this.claimStationOfJurisdiction = claimStationOfJurisdiction;
  }

  public ClaimDetail awardStationOfJurisdiction(String awardStationOfJurisdiction) {
    this.awardStationOfJurisdiction = awardStationOfJurisdiction;
    return this;
  }

  /**
   * Represents the Regional Office (RO) responsible for processing maintenance awards for the
   * veteran or claimant.
   *
   * @return awardStationOfJurisdiction
   */
  @Schema(
      name = "awardStationOfJurisdiction",
      description =
          """
          Represents the Regional Office (RO) responsible for processing maintenance
          awards for the veteran or claimant
          """)
  public String getAwardStationOfJurisdiction() {
    return awardStationOfJurisdiction;
  }

  public void setAwardStationOfJurisdiction(String awardStationOfJurisdiction) {
    this.awardStationOfJurisdiction = awardStationOfJurisdiction;
  }

  public ClaimDetail suspense(Suspense suspense) {
    this.suspense = suspense;
    return this;
  }

  /**
   * Get suspense.
   *
   * @return suspense
   */
  @Valid
  @Schema(name = "suspense")
  public Suspense getSuspense() {
    return suspense;
  }

  public void setSuspense(Suspense suspense) {
    this.suspense = suspense;
  }

  public ClaimDetail suspenseReasonCode(String suspenseReasonCode) {
    this.suspenseReasonCode = suspenseReasonCode;
    return this;
  }

  /**
   * The suspense reason code.
   *
   * @return suspenseReasonCode
   */
  @Schema(name = "suspenseReasonCode", example = "024", description = "The suspense reason code.")
  public String getSuspenseReasonCode() {
    return suspenseReasonCode;
  }

  public void setSuspenseReasonCode(String suspenseReasonCode) {
    this.suspenseReasonCode = suspenseReasonCode;
  }

  public ClaimDetail suspenseDate(OffsetDateTime suspenseDate) {
    this.suspenseDate = suspenseDate;
    return this;
  }

  /**
   * The date the claim was suspended.
   *
   * @return suspenseDate
   */
  @Valid
  @Schema(name = "suspenseDate", description = "The date the claim was suspended.")
  public OffsetDateTime getSuspenseDate() {
    return suspenseDate;
  }

  public void setSuspenseDate(OffsetDateTime suspenseDate) {
    this.suspenseDate = suspenseDate;
  }

  public ClaimDetail suspenseComment(String suspenseComment) {
    this.suspenseComment = suspenseComment;
    return this;
  }

  /**
   * Get suspenseComment.
   *
   * @return suspenseComment
   */
  @Schema(name = "suspenseComment", example = "Suspense Comment Example")
  public String getSuspenseComment() {
    return suspenseComment;
  }

  public void setSuspenseComment(String suspenseComment) {
    this.suspenseComment = suspenseComment;
  }

  public ClaimDetail waiverSubmitted(Boolean waiverSubmitted) {
    this.waiverSubmitted = waiverSubmitted;
    return this;
  }

  /**
   * Get waiverSubmitted.
   *
   * @return waiverSubmitted
   */
  @Schema(name = "waiverSubmitted")
  public Boolean getWaiverSubmitted() {
    return waiverSubmitted;
  }

  public void setWaiverSubmitted(Boolean waiverSubmitted) {
    this.waiverSubmitted = waiverSubmitted;
  }

  public ClaimDetail limitedPoa(LimitedPoa limitedPoa) {
    this.limitedPoa = limitedPoa;
    return this;
  }

  /**
   * Get limitedPoa.
   *
   * @return limitedPoa
   */
  @Valid
  @Schema(name = "limitedPoa")
  public LimitedPoa getLimitedPoa() {
    return limitedPoa;
  }

  public void setLimitedPoa(LimitedPoa limitedPoa) {
    this.limitedPoa = limitedPoa;
  }

  public ClaimDetail establishedDate(OffsetDateTime establishedDate) {
    this.establishedDate = establishedDate;
    return this;
  }

  /**
   * Date of the first lifecycle status change for the claim.
   *
   * @return establishedDate
   */
  @Valid
  @Schema(
      name = "establishedDate",
      description = "Date of the first lifecycle status change for the claim.")
  public OffsetDateTime getEstablishedDate() {
    return establishedDate;
  }

  public void setEstablishedDate(OffsetDateTime establishedDate) {
    this.establishedDate = establishedDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClaimDetail claimDetail = (ClaimDetail) o;
    return Objects.equals(this.summaryDateTime, claimDetail.summaryDateTime)
        && Objects.equals(this.lastModified, claimDetail.lastModified)
        && Objects.equals(this.claimId, claimDetail.claimId)
        && Objects.equals(this.benefitClaimType, claimDetail.benefitClaimType)
        && Objects.equals(this.phase, claimDetail.phase)
        && Objects.equals(this.phaseLastChangedDate, claimDetail.phaseLastChangedDate)
        && Objects.equals(this.claimLifecycleStatus, claimDetail.claimLifecycleStatus)
        && Objects.equals(this.completedDate, claimDetail.completedDate)
        && Objects.equals(this.claimant, claimDetail.claimant)
        && Objects.equals(this.veteran, claimDetail.veteran)
        && Objects.equals(this.receivedDate, claimDetail.receivedDate)
        && Objects.equals(this.payeeTypeCode, claimDetail.payeeTypeCode)
        && Objects.equals(this.serviceTypeCode, claimDetail.serviceTypeCode)
        && Objects.equals(this.programTypeCode, claimDetail.programTypeCode)
        && Objects.equals(this.endProductCode, claimDetail.endProductCode)
        && Objects.equals(this.tempStationOfJurisdiction, claimDetail.tempStationOfJurisdiction)
        && Objects.equals(this.claimStationOfJurisdiction, claimDetail.claimStationOfJurisdiction)
        && Objects.equals(this.awardStationOfJurisdiction, claimDetail.awardStationOfJurisdiction)
        && Objects.equals(this.suspense, claimDetail.suspense)
        && Objects.equals(this.suspenseReasonCode, claimDetail.suspenseReasonCode)
        && Objects.equals(this.suspenseDate, claimDetail.suspenseDate)
        && Objects.equals(this.suspenseComment, claimDetail.suspenseComment)
        && Objects.equals(this.waiverSubmitted, claimDetail.waiverSubmitted)
        && Objects.equals(this.limitedPoa, claimDetail.limitedPoa)
        && Objects.equals(this.establishedDate, claimDetail.establishedDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        summaryDateTime,
        lastModified,
        claimId,
        benefitClaimType,
        phase,
        phaseLastChangedDate,
        claimLifecycleStatus,
        completedDate,
        claimant,
        veteran,
        receivedDate,
        payeeTypeCode,
        serviceTypeCode,
        programTypeCode,
        endProductCode,
        tempStationOfJurisdiction,
        claimStationOfJurisdiction,
        awardStationOfJurisdiction,
        suspense,
        suspenseReasonCode,
        suspenseDate,
        suspenseComment,
        waiverSubmitted,
        limitedPoa,
        establishedDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClaimDetail {\n");
    sb.append("    summaryDateTime: ").append(toIndentedString(summaryDateTime)).append("\n");
    sb.append("    lastModified: ").append(toIndentedString(lastModified)).append("\n");
    sb.append("    claimId: ").append(toIndentedString(claimId)).append("\n");
    sb.append("    benefitClaimType: ").append(toIndentedString(benefitClaimType)).append("\n");
    sb.append("    phase: ").append(toIndentedString(phase)).append("\n");
    sb.append("    phaseLastChangedDate: ")
        .append(toIndentedString(phaseLastChangedDate))
        .append("\n");
    sb.append("    claimLifecycleStatus: ")
        .append(toIndentedString(claimLifecycleStatus))
        .append("\n");
    sb.append("    completedDate: ").append(toIndentedString(completedDate)).append("\n");
    sb.append("    claimant: ").append(toIndentedString(claimant)).append("\n");
    sb.append("    veteran: ").append(toIndentedString(veteran)).append("\n");
    sb.append("    receivedDate: ").append(toIndentedString(receivedDate)).append("\n");
    sb.append("    payeeTypeCode: ").append(toIndentedString(payeeTypeCode)).append("\n");
    sb.append("    serviceTypeCode: ").append(toIndentedString(serviceTypeCode)).append("\n");
    sb.append("    programTypeCode: ").append(toIndentedString(programTypeCode)).append("\n");
    sb.append("    endProductCode: ").append(toIndentedString(endProductCode)).append("\n");
    sb.append("    tempStationOfJurisdiction: ")
        .append(toIndentedString(tempStationOfJurisdiction))
        .append("\n");
    sb.append("    claimStationOfJurisdiction: ")
        .append(toIndentedString(claimStationOfJurisdiction))
        .append("\n");
    sb.append("    awardStationOfJurisdiction: ")
        .append(toIndentedString(awardStationOfJurisdiction))
        .append("\n");
    sb.append("    suspense: ").append(toIndentedString(suspense)).append("\n");
    sb.append("    suspenseReasonCode: ").append(toIndentedString(suspenseReasonCode)).append("\n");
    sb.append("    suspenseDate: ").append(toIndentedString(suspenseDate)).append("\n");
    sb.append("    suspenseComment: ").append(toIndentedString(suspenseComment)).append("\n");
    sb.append("    waiverSubmitted: ").append(toIndentedString(waiverSubmitted)).append("\n");
    sb.append("    limitedPoa: ").append(toIndentedString(limitedPoa)).append("\n");
    sb.append("    establishedDate: ").append(toIndentedString(establishedDate)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
