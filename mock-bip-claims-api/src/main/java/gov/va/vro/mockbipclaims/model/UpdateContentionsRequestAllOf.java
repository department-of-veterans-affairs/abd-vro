package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/** UpdateContentionsRequestAllOf. */
@JsonTypeName("UpdateContentionsRequest_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class UpdateContentionsRequestAllOf {

  @JsonProperty("updateContentions")
  @Valid
  private List<ExistingContention> updateContentions = null;

  public UpdateContentionsRequestAllOf updateContentions(
      List<ExistingContention> updateContentions) {
    this.updateContentions = updateContentions;
    return this;
  }

  /**
   * Adds contention updates.
   *
   * @param updateContentionsItem update contention
   * @return
   */
  public UpdateContentionsRequestAllOf addUpdateContentionsItem(
      ExistingContention updateContentionsItem) {
    if (this.updateContentions == null) {
      this.updateContentions = new ArrayList<>();
    }
    this.updateContentions.add(updateContentionsItem);
    return this;
  }

  /**
   * Get updateContentions.
   *
   * @return updateContentions
   */
  @Valid
  @Schema(name = "updateContentions")
  public List<ExistingContention> getUpdateContentions() {
    return updateContentions;
  }

  public void setUpdateContentions(List<ExistingContention> updateContentions) {
    this.updateContentions = updateContentions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateContentionsRequestAllOf updateContentionsRequestAllOf = (UpdateContentionsRequestAllOf) o;
    return Objects.equals(this.updateContentions, updateContentionsRequestAllOf.updateContentions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(updateContentions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateContentionsRequestAllOf {\n");
    sb.append("    updateContentions: ").append(toIndentedString(updateContentions)).append("\n");
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
