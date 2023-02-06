package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

/** test. */
@Schema(name = "AppToken", description = "test")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class AppToken {

  @JsonProperty("userId")
  private String userId;

  @JsonProperty("userKey")
  private String userKey;

  @JsonProperty("applicationName")
  private String applicationName;

  @JsonProperty("stationOfJurisdiction")
  private String stationOfJurisdiction;

  @JsonProperty("isExternal")
  private Boolean isExternal;

  public AppToken userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId.
   *
   * @return userId
   */
  @NotNull
  @Schema(name = "userId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public AppToken userKey(String userKey) {
    this.userKey = userKey;
    return this;
  }

  /**
   * Get userKey.
   *
   * @return userKey
   */
  @NotNull
  @Schema(name = "userKey")
  public String getUserKey() {
    return userKey;
  }

  public void setUserKey(String userKey) {
    this.userKey = userKey;
  }

  public AppToken applicationName(String applicationName) {
    this.applicationName = applicationName;
    return this;
  }

  /**
   * Get applicationName.
   *
   * @return applicationName
   */
  @Schema(name = "applicationName")
  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public AppToken stationOfJurisdiction(String stationOfJurisdiction) {
    this.stationOfJurisdiction = stationOfJurisdiction;
    return this;
  }

  /**
   * Get stationOfJurisdiction.
   *
   * @return stationOfJurisdiction
   */
  @Schema(name = "stationOfJurisdiction")
  public String getStationOfJurisdiction() {
    return stationOfJurisdiction;
  }

  public void setStationOfJurisdiction(String stationOfJurisdiction) {
    this.stationOfJurisdiction = stationOfJurisdiction;
  }

  public AppToken isExternal(Boolean isExternal) {
    this.isExternal = isExternal;
    return this;
  }

  /**
   * Get isExternal.
   *
   * @return isExternal
   */
  @Schema(name = "isExternal")
  public Boolean getIsExternal() {
    return isExternal;
  }

  public void setIsExternal(Boolean isExternal) {
    this.isExternal = isExternal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AppToken appToken = (AppToken) o;
    return Objects.equals(this.userId, appToken.userId)
        && Objects.equals(this.userKey, appToken.userKey)
        && Objects.equals(this.applicationName, appToken.applicationName)
        && Objects.equals(this.stationOfJurisdiction, appToken.stationOfJurisdiction)
        && Objects.equals(this.isExternal, appToken.isExternal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, userKey, applicationName, stationOfJurisdiction, isExternal);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AppToken {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    userKey: ").append(toIndentedString(userKey)).append("\n");
    sb.append("    applicationName: ").append(toIndentedString(applicationName)).append("\n");
    sb.append("    stationOfJurisdiction: ")
        .append(toIndentedString(stationOfJurisdiction))
        .append("\n");
    sb.append("    isExternal: ").append(toIndentedString(isExternal)).append("\n");
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
