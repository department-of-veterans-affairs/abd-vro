package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** ContentionDetailAllOf. */
@JsonTypeName("ContentionDetail_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ContentionDetailAllOf {

  @JsonProperty("contentionHistory")
  @Valid
  private List<ContentionHistory> contentionHistory = null;

  /**
   * Contention history.
   *
   * @param contentionHistory input
   * @return all of
   */
  public ContentionDetailAllOf contentionHistory(List<ContentionHistory> contentionHistory) {
    this.contentionHistory = contentionHistory;
    return this;
  }

  /**
   * Adds contention history.
   *
   * @param contentionHistoryItem input
   * @return all of
   */
  public ContentionDetailAllOf addContentionHistoryItem(ContentionHistory contentionHistoryItem) {
    if (this.contentionHistory == null) {
      this.contentionHistory = new ArrayList<>();
    }
    this.contentionHistory.add(contentionHistoryItem);
    return this;
  }

  /**
   * Get contentionHistory.
   *
   * @return contentionHistory
   */
  @Valid
  @Schema(name = "contentionHistory")
  public List<ContentionHistory> getContentionHistory() {
    return contentionHistory;
  }

  public void setContentionHistory(List<ContentionHistory> contentionHistory) {
    this.contentionHistory = contentionHistory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentionDetailAllOf contentionDetailAllOf = (ContentionDetailAllOf) o;
    return Objects.equals(this.contentionHistory, contentionDetailAllOf.contentionHistory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentionHistory);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentionDetailAllOf {\n");
    sb.append("    contentionHistory: ").append(toIndentedString(contentionHistory)).append("\n");
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
