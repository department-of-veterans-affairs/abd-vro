package gov.va.vro.model.bipevidence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/** Alternative document type information for UI consumption. */
@Schema(
    name = "alternativeDocumentType",
    description = "Alternative document type information for UI consumption.")
@JsonTypeName("alternativeDocumentType")
public class AlternativeDocumentType {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("createDateTime")
  private String createDateTime;

  @JsonProperty("modifiedDateTime")
  private String modifiedDateTime;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  public AlternativeDocumentType id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   *
   * @return id
   */
  @Schema(name = "id", required = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AlternativeDocumentType createDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
    return this;
  }

  /**
   * Get createDateTime
   *
   * @return createDateTime
   */
  @Schema(name = "createDateTime", required = false)
  public String getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
  }

  public AlternativeDocumentType modifiedDateTime(String modifiedDateTime) {
    this.modifiedDateTime = modifiedDateTime;
    return this;
  }

  /**
   * Get modifiedDateTime
   *
   * @return modifiedDateTime
   */
  @Schema(name = "modifiedDateTime", required = false)
  public String getModifiedDateTime() {
    return modifiedDateTime;
  }

  public void setModifiedDateTime(String modifiedDateTime) {
    this.modifiedDateTime = modifiedDateTime;
  }

  public AlternativeDocumentType name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   *
   * @return name
   */
  @Schema(name = "name", required = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AlternativeDocumentType description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   *
   * @return description
   */
  @Schema(name = "description", required = false)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AlternativeDocumentType alternativeDocumentType = (AlternativeDocumentType) o;
    return Objects.equals(this.id, alternativeDocumentType.id)
        && Objects.equals(this.createDateTime, alternativeDocumentType.createDateTime)
        && Objects.equals(this.modifiedDateTime, alternativeDocumentType.modifiedDateTime)
        && Objects.equals(this.name, alternativeDocumentType.name)
        && Objects.equals(this.description, alternativeDocumentType.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, createDateTime, modifiedDateTime, name, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AlternativeDocumentType {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    createDateTime: ").append(toIndentedString(createDateTime)).append("\n");
    sb.append("    modifiedDateTime: ").append(toIndentedString(modifiedDateTime)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
