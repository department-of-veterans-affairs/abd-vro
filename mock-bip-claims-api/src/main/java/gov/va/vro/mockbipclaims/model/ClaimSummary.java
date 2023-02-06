package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/**
 * An object to summarize the data in a claim. A claim is a formal request for benefits that is
 * submitted by a Veteran, or by a person or organization authorized to act on a Veteran&#39;s
 * behalf.
 */
@Schema(
    name = "ClaimSummary",
    description =
        """
        An object to summarize the data in a claim. A claim is a formal request for benefits
        that is submitted by a Veteran, or by a person or organization authorized to act
        on a Veteran's behalf.
        """)
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ClaimSummary {

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

  public ClaimSummary summaryDateTime(OffsetDateTime summaryDateTime) {
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

  public ClaimSummary lastModified(OffsetDateTime lastModified) {
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

  public ClaimSummary claimId(Long claimId) {
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

  public ClaimSummary benefitClaimType(BenefitClaimType benefitClaimType) {
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

  public ClaimSummary phase(PhaseType phase) {
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

  public ClaimSummary phaseLastChangedDate(OffsetDateTime phaseLastChangedDate) {
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

  public ClaimSummary claimLifecycleStatus(String claimLifecycleStatus) {
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

  public ClaimSummary completedDate(OffsetDateTime completedDate) {
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

  public ClaimSummary claimant(Claimant claimant) {
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

  public ClaimSummary veteran(Veteran veteran) {
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

  public ClaimSummary receivedDate(OffsetDateTime receivedDate) {
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

  public ClaimSummary payeeTypeCode(String payeeTypeCode) {
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

  public ClaimSummary serviceTypeCode(String serviceTypeCode) {
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

  public ClaimSummary programTypeCode(String programTypeCode) {
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

  public ClaimSummary endProductCode(String endProductCode) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClaimSummary claimSummary = (ClaimSummary) o;
    return Objects.equals(this.summaryDateTime, claimSummary.summaryDateTime)
        && Objects.equals(this.lastModified, claimSummary.lastModified)
        && Objects.equals(this.claimId, claimSummary.claimId)
        && Objects.equals(this.benefitClaimType, claimSummary.benefitClaimType)
        && Objects.equals(this.phase, claimSummary.phase)
        && Objects.equals(this.phaseLastChangedDate, claimSummary.phaseLastChangedDate)
        && Objects.equals(this.claimLifecycleStatus, claimSummary.claimLifecycleStatus)
        && Objects.equals(this.completedDate, claimSummary.completedDate)
        && Objects.equals(this.claimant, claimSummary.claimant)
        && Objects.equals(this.veteran, claimSummary.veteran)
        && Objects.equals(this.receivedDate, claimSummary.receivedDate)
        && Objects.equals(this.payeeTypeCode, claimSummary.payeeTypeCode)
        && Objects.equals(this.serviceTypeCode, claimSummary.serviceTypeCode)
        && Objects.equals(this.programTypeCode, claimSummary.programTypeCode)
        && Objects.equals(this.endProductCode, claimSummary.endProductCode);
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
        endProductCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClaimSummary {\n");
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
