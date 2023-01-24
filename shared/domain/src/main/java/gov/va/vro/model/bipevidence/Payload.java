package gov.va.vro.model.bipevidence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import gov.va.vro.model.bipevidence.request.UploadProviderDataRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/** Payload containing content name and the provider specific data. */
@Schema(
    name = "payload",
    description = "Payload containing content name and the provider specific data.")
@JsonTypeName("payload")
public class Payload {

  @JsonProperty("contentName")
  private String contentName;

  @JsonProperty("providerData")
  private UploadProviderDataRequest providerData;

  public Payload contentName(String contentName) {
    this.contentName = contentName;
    return this;
  }

  /**
   * The content name of the document being uploaded. This must be unique for the folder being
   * uploaded to. For instance the document \"pdf.pdf\" cannot be uploaded twice for fileNumber
   * 987654321.
   *
   * @return contentName
   */
  @Pattern(regexp = "^[a-zA-Z0-9 Q`'~=+#^@$&-_.(){};[]E]+.[a-zA-Z]{3,4}$")
  @Size(min = 4, max = 256)
  @Schema(
      name = "contentName",
      description =
          """
          The content name of the document being uploaded.
          For instance the document \\"pdf.pdf\\" cannot be uploaded twice for
          fileNumber 987654321.
          """,
      required = false)
  public String getContentName() {
    return contentName;
  }

  public void setContentName(String contentName) {
    this.contentName = contentName;
  }

  public Payload providerData(UploadProviderDataRequest providerData) {
    this.providerData = providerData;
    return this;
  }

  /**
   * Get providerData.
   *
   * @return providerData
   */
  @Valid
  @Schema(name = "providerData", required = false)
  public UploadProviderDataRequest getProviderData() {
    return providerData;
  }

  public void setProviderData(UploadProviderDataRequest providerData) {
    this.providerData = providerData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Payload payload = (Payload) o;
    return Objects.equals(this.contentName, payload.contentName)
        && Objects.equals(this.providerData, payload.providerData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentName, providerData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Payload {\n");
    sb.append("    contentName: ").append(toIndentedString(contentName)).append("\n");
    sb.append("    providerData: ").append(toIndentedString(providerData)).append("\n");
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
