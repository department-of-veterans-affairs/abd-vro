package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import javax.annotation.Generated;

/** Content sources for UI consumption. */
@Schema(name = "contentSource", description = "Content sources for UI consumption.")
@JsonTypeName("contentSource")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-01-22T14:21:59.944759-05:00[America/New_York]")
public class ContentSource {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("createDateTime")
  private String createDateTime;

  @JsonProperty("name")
  private String name;

  public ContentSource id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id.
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

  public ContentSource createDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
    return this;
  }

  /**
   * Get createDateTime.
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

  public ContentSource name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name.
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentSource contentSource = (ContentSource) o;
    return Objects.equals(this.id, contentSource.id)
        && Objects.equals(this.createDateTime, contentSource.createDateTime)
        && Objects.equals(this.name, contentSource.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, createDateTime, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentSource {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    createDateTime: ").append(toIndentedString(createDateTime)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
