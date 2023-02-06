package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/** CreateContentionsRequestAllOf. */
@JsonTypeName("CreateContentionsRequest_allOf")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class CreateContentionsRequestAllOf {

  @JsonProperty("createContentions")
  @Valid
  private List<Contention> createContentions = new ArrayList<>();

  public CreateContentionsRequestAllOf createContentions(List<Contention> createContentions) {
    this.createContentions = createContentions;
    return this;
  }

  public CreateContentionsRequestAllOf addCreateContentionsItem(Contention createContentionsItem) {
    this.createContentions.add(createContentionsItem);
    return this;
  }

  /**
   * Get createContentions.
   *
   * @return createContentions
   */
  @NotNull
  @Valid
  @Schema(name = "createContentions")
  public List<Contention> getCreateContentions() {
    return createContentions;
  }

  public void setCreateContentions(List<Contention> createContentions) {
    this.createContentions = createContentions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateContentionsRequestAllOf createContentionsRequestAllOf = (CreateContentionsRequestAllOf) o;
    return Objects.equals(this.createContentions, createContentionsRequestAllOf.createContentions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(createContentions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateContentionsRequestAllOf {\n");
    sb.append("    createContentions: ").append(toIndentedString(createContentions)).append("\n");
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
