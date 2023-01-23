package org.openapitools.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;

/**
 * UploadResponse
 */

@JsonTypeName("uploadResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-22T14:21:59.944759-05:00[America/New_York]")
public class UploadResponse {

  @JsonProperty("owner")
  private String owner;

  @JsonProperty("uuid")
  private UUID uuid;

  @JsonProperty("currentVersionUuid")
  private UUID currentVersionUuid;

  @JsonProperty("md5")
  private String md5;

  @JsonProperty("conversionInformation")
  private ConversionInfo conversionInformation;

  public UploadResponse owner(String owner) {
    this.owner = owner;
    return this;
  }

  /**
   * String representation of the document's owner
   * @return owner
  */
  
  @Schema(name = "owner", example = "VETERAN:FILENUMBER:987267855", description = "String representation of the document's owner", required = false)
  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public UploadResponse uuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * UUID representing the file as a whole. This is used for all primary VEFS-Operations.
   * @return uuid
  */
  @Valid 
  @Schema(name = "uuid", example = "c30626c9-954d-4dd1-9f70-1e38756d9d97", description = "UUID representing the file as a whole. This is used for all primary VEFS-Operations.", required = false)
  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public UploadResponse currentVersionUuid(UUID currentVersionUuid) {
    this.currentVersionUuid = currentVersionUuid;
    return this;
  }

  /**
   * UUID representing the single point-in-time version of the document.
   * @return currentVersionUuid
  */
  @Valid 
  @Schema(name = "currentVersionUuid", example = "c30626c9-954d-4dd1-9f70-1e38756d9d98", description = "UUID representing the single point-in-time version of the document.", required = false)
  public UUID getCurrentVersionUuid() {
    return currentVersionUuid;
  }

  public void setCurrentVersionUuid(UUID currentVersionUuid) {
    this.currentVersionUuid = currentVersionUuid;
  }

  public UploadResponse md5(String md5) {
    this.md5 = md5;
    return this;
  }

  /**
   * MD5 Hash of the File field on upload.
   * @return md5
  */
  
  @Schema(name = "md5", example = "32c31506acefa9f125c2a790ed1e675f", description = "MD5 Hash of the File field on upload.", required = false)
  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public UploadResponse conversionInformation(ConversionInfo conversionInformation) {
    this.conversionInformation = conversionInformation;
    return this;
  }

  /**
   * Get conversionInformation
   * @return conversionInformation
  */
  @Valid 
  @Schema(name = "conversionInformation", required = false)
  public ConversionInfo getConversionInformation() {
    return conversionInformation;
  }

  public void setConversionInformation(ConversionInfo conversionInformation) {
    this.conversionInformation = conversionInformation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UploadResponse uploadResponse = (UploadResponse) o;
    return Objects.equals(this.owner, uploadResponse.owner) &&
        Objects.equals(this.uuid, uploadResponse.uuid) &&
        Objects.equals(this.currentVersionUuid, uploadResponse.currentVersionUuid) &&
        Objects.equals(this.md5, uploadResponse.md5) &&
        Objects.equals(this.conversionInformation, uploadResponse.conversionInformation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(owner, uuid, currentVersionUuid, md5, conversionInformation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UploadResponse {\n");
    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    currentVersionUuid: ").append(toIndentedString(currentVersionUuid)).append("\n");
    sb.append("    md5: ").append(toIndentedString(md5)).append("\n");
    sb.append("    conversionInformation: ").append(toIndentedString(conversionInformation)).append("\n");
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

