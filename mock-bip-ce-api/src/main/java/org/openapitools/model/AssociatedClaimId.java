package org.openapitools.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Filter based on the claims associated to the documents.
 */

@Schema(name = "associatedClaimId", description = "Filter based on the claims associated to the documents.")
@JsonTypeName("associatedClaimId")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-22T14:21:59.944759-05:00[America/New_York]")
public class AssociatedClaimId {

  /**
   * Gets or Sets evaluationType
   */
  public enum EvaluationTypeEnum {
    EQUALS("EQUALS");

    private String value;

    EvaluationTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EvaluationTypeEnum fromValue(String value) {
      for (EvaluationTypeEnum b : EvaluationTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("evaluationType")
  private EvaluationTypeEnum evaluationType;

  @JsonProperty("value")
  private String value;

  public AssociatedClaimId evaluationType(EvaluationTypeEnum evaluationType) {
    this.evaluationType = evaluationType;
    return this;
  }

  /**
   * Get evaluationType
   * @return evaluationType
  */
  
  @Schema(name = "evaluationType", required = false)
  public EvaluationTypeEnum getEvaluationType() {
    return evaluationType;
  }

  public void setEvaluationType(EvaluationTypeEnum evaluationType) {
    this.evaluationType = evaluationType;
  }

  public AssociatedClaimId value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  
  @Schema(name = "value", example = "[137]", required = false)
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssociatedClaimId associatedClaimId = (AssociatedClaimId) o;
    return Objects.equals(this.evaluationType, associatedClaimId.evaluationType) &&
        Objects.equals(this.value, associatedClaimId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(evaluationType, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssociatedClaimId {\n");
    sb.append("    evaluationType: ").append(toIndentedString(evaluationType)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

