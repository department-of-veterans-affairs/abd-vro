package org.openapitools.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;

/**
 * UploadRequest
 */

@JsonTypeName("uploadRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-22T15:12:32.549348-05:00[America/New_York]")
public class UploadRequest {

  @JsonProperty("payload")
  private Payload payload;

  @JsonProperty("file")
  private org.springframework.core.io.Resource file;

  public UploadRequest payload(Payload payload) {
    this.payload = payload;
    return this;
  }

  /**
   * Get payload
   * @return payload
  */
  @Valid 
  @Schema(name = "payload", required = false)
  public Payload getPayload() {
    return payload;
  }

  public void setPayload(Payload payload) {
    this.payload = payload;
  }

  public UploadRequest file(org.springframework.core.io.Resource file) {
    this.file = file;
    return this;
  }

  /**
   * Get file
   * @return file
  */
  @Valid 
  @Schema(name = "file", required = false)
  public org.springframework.core.io.Resource getFile() {
    return file;
  }

  public void setFile(org.springframework.core.io.Resource file) {
    this.file = file;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UploadRequest uploadRequest = (UploadRequest) o;
    return Objects.equals(this.payload, uploadRequest.payload) &&
        Objects.equals(this.file, uploadRequest.file);
  }

  @Override
  public int hashCode() {
    return Objects.hash(payload, file);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UploadRequest {\n");
    sb.append("    payload: ").append(toIndentedString(payload)).append("\n");
    sb.append("    file: ").append(toIndentedString(file)).append("\n");
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

