package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** ContentionSummaryAllOf. */
@JsonTypeName("ContentionSummary_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class ContentionSummaryAllOf {

  @JsonProperty("summaryDateTime")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime summaryDateTime;

  public ContentionSummaryAllOf summaryDateTime(OffsetDateTime summaryDateTime) {
    this.summaryDateTime = summaryDateTime;
    return this;
  }

  /**
   * Get summaryDateTime.
   *
   * @return summaryDateTime
   */
  @Valid
  @Schema(name = "summaryDateTime")
  public OffsetDateTime getSummaryDateTime() {
    return summaryDateTime;
  }

  public void setSummaryDateTime(OffsetDateTime summaryDateTime) {
    this.summaryDateTime = summaryDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentionSummaryAllOf contentionSummaryAllOf = (ContentionSummaryAllOf) o;
    return Objects.equals(this.summaryDateTime, contentionSummaryAllOf.summaryDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(summaryDateTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentionSummaryAllOf {\n");
    sb.append("    summaryDateTime: ").append(toIndentedString(summaryDateTime)).append("\n");
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
