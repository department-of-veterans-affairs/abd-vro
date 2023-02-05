package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;

/** Holds name and boolean status of a feature flag, as found in bip-vetservices-claims.yml. */
@Schema(
    name = "FeatureFlag",
    description =
        "Holds name and boolean status of a feature flag, as found in bip-vetservices-claims.yml")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class FeatureFlag {

  @JsonProperty("featureFlagName")
  private String featureFlagName;

  @JsonProperty("featureFlagStatus")
  private Boolean featureFlagStatus;

  public FeatureFlag featureFlagName(String featureFlagName) {
    this.featureFlagName = featureFlagName;
    return this;
  }

  /**
   * Get featureFlagName.
   *
   * @return featureFlagName
   */
  @Schema(name = "featureFlagName")
  public String getFeatureFlagName() {
    return featureFlagName;
  }

  public void setFeatureFlagName(String featureFlagName) {
    this.featureFlagName = featureFlagName;
  }

  public FeatureFlag featureFlagStatus(Boolean featureFlagStatus) {
    this.featureFlagStatus = featureFlagStatus;
    return this;
  }

  /**
   * Get featureFlagStatus.
   *
   * @return featureFlagStatus
   */
  @Schema(name = "featureFlagStatus")
  public Boolean getFeatureFlagStatus() {
    return featureFlagStatus;
  }

  public void setFeatureFlagStatus(Boolean featureFlagStatus) {
    this.featureFlagStatus = featureFlagStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FeatureFlag featureFlag = (FeatureFlag) o;
    return Objects.equals(this.featureFlagName, featureFlag.featureFlagName)
        && Objects.equals(this.featureFlagStatus, featureFlag.featureFlagStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(featureFlagName, featureFlagStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FeatureFlag {\n");
    sb.append("    featureFlagName: ").append(toIndentedString(featureFlagName)).append("\n");
    sb.append("    featureFlagStatus: ").append(toIndentedString(featureFlagStatus)).append("\n");
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
