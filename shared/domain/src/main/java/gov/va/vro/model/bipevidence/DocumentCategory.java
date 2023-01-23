package gov.va.vro.model.bipevidence;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Document type category information for UI consumption.
 */

@Schema(name = "documentCategory", description = "Document type category information for UI consumption.")
@JsonTypeName("documentCategory")
public class DocumentCategory {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("createDateTime")
  private String createDateTime;

  @JsonProperty("modifiedDateTime")
  private String modifiedDateTime;

  @JsonProperty("description")
  private String description;

  @JsonProperty("subDescription")
  private String subDescription;

  public DocumentCategory id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", required = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DocumentCategory createDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
    return this;
  }

  /**
   * Get createDateTime
   * @return createDateTime
  */
  
  @Schema(name = "createDateTime", required = false)
  public String getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
  }

  public DocumentCategory modifiedDateTime(String modifiedDateTime) {
    this.modifiedDateTime = modifiedDateTime;
    return this;
  }

  /**
   * Get modifiedDateTime
   * @return modifiedDateTime
  */
  
  @Schema(name = "modifiedDateTime", required = false)
  public String getModifiedDateTime() {
    return modifiedDateTime;
  }

  public void setModifiedDateTime(String modifiedDateTime) {
    this.modifiedDateTime = modifiedDateTime;
  }

  public DocumentCategory description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  
  @Schema(name = "description", required = false)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public DocumentCategory subDescription(String subDescription) {
    this.subDescription = subDescription;
    return this;
  }

  /**
   * Get subDescription
   * @return subDescription
  */
  
  @Schema(name = "subDescription", required = false)
  public String getSubDescription() {
    return subDescription;
  }

  public void setSubDescription(String subDescription) {
    this.subDescription = subDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentCategory documentCategory = (DocumentCategory) o;
    return Objects.equals(this.id, documentCategory.id) &&
        Objects.equals(this.createDateTime, documentCategory.createDateTime) &&
        Objects.equals(this.modifiedDateTime, documentCategory.modifiedDateTime) &&
        Objects.equals(this.description, documentCategory.description) &&
        Objects.equals(this.subDescription, documentCategory.subDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, createDateTime, modifiedDateTime, description, subDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DocumentCategory {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    createDateTime: ").append(toIndentedString(createDateTime)).append("\n");
    sb.append("    modifiedDateTime: ").append(toIndentedString(modifiedDateTime)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    subDescription: ").append(toIndentedString(subDescription)).append("\n");
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

