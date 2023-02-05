package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Max;

/**
 * Limited Power of Attorney (POA) representation. A Veteran Service Organization (VSO) or other
 * individual appointed by the Veteran or claimant who represents their interests.Limited
 * representation is only for a specific claim, versus a general POA that can be associated with all
 * claims for a claimant.
 */
@Schema(
    name = "LimitedPoa",
    description =
        """
        Limited Power of Attorney (POA) representation. A Veteran Service Organization (VSO)
        or other individual appointed by the Veteran or claimant who represents their
        interests.Limited representation is only for a specific claim, versus a general POA
        that can be associated with all claims for a claimant.
        """)
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class LimitedPoa {

  @JsonProperty("veteranParticipantId")
  private Long veteranParticipantId;

  @JsonProperty("claimantParticipantId")
  private Long claimantParticipantId;

  @JsonProperty("poaParticipantId")
  private Long poaParticipantId;

  @JsonProperty("poaCode")
  private String poaCode;

  @JsonProperty("startDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  public LimitedPoa veteranParticipantId(Long veteranParticipantId) {
    this.veteranParticipantId = veteranParticipantId;
    return this;
  }

  /**
   * Participant ID of veteran maximum: 999999999999999.
   *
   * @return veteranParticipantId
   */
  @Max(999999999999999L)
  @Schema(
      name = "veteranParticipantId",
      example = "307405",
      description = "Participant ID of veteran")
  public Long getVeteranParticipantId() {
    return veteranParticipantId;
  }

  public void setVeteranParticipantId(Long veteranParticipantId) {
    this.veteranParticipantId = veteranParticipantId;
  }

  public LimitedPoa claimantParticipantId(Long claimantParticipantId) {
    this.claimantParticipantId = claimantParticipantId;
    return this;
  }

  /**
   * Participant ID of claimant maximum: 999999999999999.
   *
   * @return claimantParticipantId
   */
  @Max(999999999999999L)
  @Schema(
      name = "claimantParticipantId",
      example = "307405",
      description = "Participant ID of claimant")
  public Long getClaimantParticipantId() {
    return claimantParticipantId;
  }

  public void setClaimantParticipantId(Long claimantParticipantId) {
    this.claimantParticipantId = claimantParticipantId;
  }

  public LimitedPoa poaParticipantId(Long poaParticipantId) {
    this.poaParticipantId = poaParticipantId;
    return this;
  }

  /**
   * Participant ID of limited POA maximum: 999999999999999.
   *
   * @return poaParticipantId
   */
  @Max(999999999999999L)
  @Schema(
      name = "poaParticipantId",
      example = "307405",
      description = "Participant ID of limited POA")
  public Long getPoaParticipantId() {
    return poaParticipantId;
  }

  public void setPoaParticipantId(Long poaParticipantId) {
    this.poaParticipantId = poaParticipantId;
  }

  public LimitedPoa poaCode(String poaCode) {
    this.poaCode = poaCode;
    return this;
  }

  /**
   * Code of limited POA.
   *
   * @return poaCode
   */
  @Schema(name = "poaCode", example = "6", description = "Code of limited POA")
  public String getPoaCode() {
    return poaCode;
  }

  public void setPoaCode(String poaCode) {
    this.poaCode = poaCode;
  }

  public LimitedPoa startDate(OffsetDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Start date of the POA representation for this claim.
   *
   * @return startDate
   */
  @Valid
  @Schema(name = "startDate", description = "Start date of the POA representation for this claim")
  public OffsetDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(OffsetDateTime startDate) {
    this.startDate = startDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LimitedPoa limitedPoa = (LimitedPoa) o;
    return Objects.equals(this.veteranParticipantId, limitedPoa.veteranParticipantId)
        && Objects.equals(this.claimantParticipantId, limitedPoa.claimantParticipantId)
        && Objects.equals(this.poaParticipantId, limitedPoa.poaParticipantId)
        && Objects.equals(this.poaCode, limitedPoa.poaCode)
        && Objects.equals(this.startDate, limitedPoa.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        veteranParticipantId, claimantParticipantId, poaParticipantId, poaCode, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LimitedPoa {\n");
    sb.append("    veteranParticipantId: ")
        .append(toIndentedString(veteranParticipantId))
        .append("\n");
    sb.append("    claimantParticipantId: ")
        .append(toIndentedString(claimantParticipantId))
        .append("\n");
    sb.append("    poaParticipantId: ").append(toIndentedString(poaParticipantId)).append("\n");
    sb.append("    poaCode: ").append(toIndentedString(poaCode)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
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
