package gov.va.vro.model.bipevidence;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Information only present if the document has been converted. Details the previous version mime type, md5, and time of file conversion.
 */

@Schema(name = "conversionInfo", description = "Information only present if the document has been converted. Details the previous version mime type, md5, and time of file conversion.")
@JsonTypeName("conversionInfo")
public class ConversionInfo {

  @JsonProperty("versionUuid")
  private UUID versionUuid;

  @JsonProperty("mimeType")
  private String mimeType;

  @JsonProperty("md5")
  private String md5;

  @JsonProperty("uploadedDateTime")
  private String uploadedDateTime;

  public ConversionInfo versionUuid(UUID versionUuid) {
    this.versionUuid = versionUuid;
    return this;
  }

  /**
   * UUID identifying the particular version.
   * @return versionUuid
  */
  @Valid 
  @Schema(name = "versionUuid", description = "UUID identifying the particular version.", required = false)
  public UUID getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(UUID versionUuid) {
    this.versionUuid = versionUuid;
  }

  public ConversionInfo mimeType(String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

  /**
   * Mime Type of the indicated versionUuid.
   * @return mimeType
  */
  
  @Schema(name = "mimeType", description = "Mime Type of the indicated versionUuid.", required = false)
  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public ConversionInfo md5(String md5) {
    this.md5 = md5;
    return this;
  }

  /**
   * MD5 hash code of the particular version.
   * @return md5
  */
  
  @Schema(name = "md5", description = "MD5 hash code of the particular version.", required = false)
  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public ConversionInfo uploadedDateTime(String uploadedDateTime) {
    this.uploadedDateTime = uploadedDateTime;
    return this;
  }

  /**
   * Date and time the document was uploaded.
   * @return uploadedDateTime
  */
  
  @Schema(name = "uploadedDateTime", description = "Date and time the document was uploaded.", required = false)
  public String getUploadedDateTime() {
    return uploadedDateTime;
  }

  public void setUploadedDateTime(String uploadedDateTime) {
    this.uploadedDateTime = uploadedDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConversionInfo conversionInfo = (ConversionInfo) o;
    return Objects.equals(this.versionUuid, conversionInfo.versionUuid) &&
        Objects.equals(this.mimeType, conversionInfo.mimeType) &&
        Objects.equals(this.md5, conversionInfo.md5) &&
        Objects.equals(this.uploadedDateTime, conversionInfo.uploadedDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(versionUuid, mimeType, md5, uploadedDateTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConversionInfo {\n");
    sb.append("    versionUuid: ").append(toIndentedString(versionUuid)).append("\n");
    sb.append("    mimeType: ").append(toIndentedString(mimeType)).append("\n");
    sb.append("    md5: ").append(toIndentedString(md5)).append("\n");
    sb.append("    uploadedDateTime: ").append(toIndentedString(uploadedDateTime)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

