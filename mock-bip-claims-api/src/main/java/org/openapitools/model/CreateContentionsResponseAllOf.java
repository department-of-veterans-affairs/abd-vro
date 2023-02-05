package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** CreateContentionsResponseAllOf. */
@JsonTypeName("CreateContentionsResponse_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class CreateContentionsResponseAllOf {

  @JsonProperty("contentionIds")
  @Valid
  private List<Long> contentionIds = null;

  public CreateContentionsResponseAllOf contentionIds(List<Long> contentionIds) {
    this.contentionIds = contentionIds;
    return this;
  }

  /**
   * Adds contention id.
   *
   * @param contentionIdsItem contention id
   * @return response
   */
  public CreateContentionsResponseAllOf addContentionIdsItem(Long contentionIdsItem) {
    if (this.contentionIds == null) {
      this.contentionIds = new ArrayList<>();
    }
    this.contentionIds.add(contentionIdsItem);
    return this;
  }

  /**
   * Get contentionIds.
   *
   * @return contentionIds
   */
  @Schema(name = "contentionIds")
  public List<Long> getContentionIds() {
    return contentionIds;
  }

  public void setContentionIds(List<Long> contentionIds) {
    this.contentionIds = contentionIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateContentionsResponseAllOf createContentionsResponseAllOf =
        (CreateContentionsResponseAllOf) o;
    return Objects.equals(this.contentionIds, createContentionsResponseAllOf.contentionIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentionIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateContentionsResponseAllOf {\n");
    sb.append("    contentionIds: ").append(toIndentedString(contentionIds)).append("\n");
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
