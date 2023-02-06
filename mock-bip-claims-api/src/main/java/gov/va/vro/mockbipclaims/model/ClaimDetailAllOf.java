package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** ClaimDetailAllOf. */
@JsonTypeName("ClaimDetail_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ClaimDetailAllOf {

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

  public ClaimDetailAllOf tempStationOfJurisdiction(String tempStationOfJurisdiction) {
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

  public ClaimDetailAllOf claimStationOfJurisdiction(String claimStationOfJurisdiction) {
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

  public ClaimDetailAllOf awardStationOfJurisdiction(String awardStationOfJurisdiction) {
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
          Represents the Regional Office (RO) responsible for processing maintenance awards
          for the veteran or claimant
          """)
  public String getAwardStationOfJurisdiction() {
    return awardStationOfJurisdiction;
  }

  public void setAwardStationOfJurisdiction(String awardStationOfJurisdiction) {
    this.awardStationOfJurisdiction = awardStationOfJurisdiction;
  }

  public ClaimDetailAllOf suspense(Suspense suspense) {
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

  public ClaimDetailAllOf suspenseReasonCode(String suspenseReasonCode) {
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

  public ClaimDetailAllOf suspenseDate(OffsetDateTime suspenseDate) {
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

  public ClaimDetailAllOf suspenseComment(String suspenseComment) {
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

  public ClaimDetailAllOf waiverSubmitted(Boolean waiverSubmitted) {
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

  public ClaimDetailAllOf limitedPoa(LimitedPoa limitedPoa) {
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

  public ClaimDetailAllOf establishedDate(OffsetDateTime establishedDate) {
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
    ClaimDetailAllOf claimDetailAllOf = (ClaimDetailAllOf) o;
    return Objects.equals(
            this.tempStationOfJurisdiction, claimDetailAllOf.tempStationOfJurisdiction)
        && Objects.equals(
            this.claimStationOfJurisdiction, claimDetailAllOf.claimStationOfJurisdiction)
        && Objects.equals(
            this.awardStationOfJurisdiction, claimDetailAllOf.awardStationOfJurisdiction)
        && Objects.equals(this.suspense, claimDetailAllOf.suspense)
        && Objects.equals(this.suspenseReasonCode, claimDetailAllOf.suspenseReasonCode)
        && Objects.equals(this.suspenseDate, claimDetailAllOf.suspenseDate)
        && Objects.equals(this.suspenseComment, claimDetailAllOf.suspenseComment)
        && Objects.equals(this.waiverSubmitted, claimDetailAllOf.waiverSubmitted)
        && Objects.equals(this.limitedPoa, claimDetailAllOf.limitedPoa)
        && Objects.equals(this.establishedDate, claimDetailAllOf.establishedDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
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
    sb.append("class ClaimDetailAllOf {\n");
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
