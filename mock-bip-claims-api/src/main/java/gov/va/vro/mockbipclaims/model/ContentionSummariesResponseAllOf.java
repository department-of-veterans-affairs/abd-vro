package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** ContentionSummariesResponseAllOf. */
@JsonTypeName("ContentionSummariesResponse_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ContentionSummariesResponseAllOf {

  @JsonProperty("contentions")
  @Valid
  private List<ContentionSummary> contentions = null;

  public ContentionSummariesResponseAllOf contentions(List<ContentionSummary> contentions) {
    this.contentions = contentions;
    return this;
  }

  /**
   * Adds a contention ite,.
   *
   * @param contentionsItem contention item
   * @return rsponse
   */
  public ContentionSummariesResponseAllOf addContentionsItem(ContentionSummary contentionsItem) {
    if (this.contentions == null) {
      this.contentions = new ArrayList<>();
    }
    this.contentions.add(contentionsItem);
    return this;
  }

  /**
   * Get contentions.
   *
   * @return contentions
   */
  @Valid
  @Schema(name = "contentions")
  public List<ContentionSummary> getContentions() {
    return contentions;
  }

  public void setContentions(List<ContentionSummary> contentions) {
    this.contentions = contentions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentionSummariesResponseAllOf contentionSummariesResponseAllOf =
        (ContentionSummariesResponseAllOf) o;
    return Objects.equals(this.contentions, contentionSummariesResponseAllOf.contentions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contentions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentionSummariesResponseAllOf {\n");
    sb.append("    contentions: ").append(toIndentedString(contentions)).append("\n");
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
