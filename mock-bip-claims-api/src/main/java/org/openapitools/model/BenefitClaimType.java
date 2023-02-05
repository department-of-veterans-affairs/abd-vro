package org.openapitools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;

/**
 * The object used to specify a claim type. This object gives a higher level of granularity to claim
 * types, especially when there are only 9 modifiers. Using this object will remove the need to
 * specify EP code or label in separate fields. The values reflected in this object reflect the
 * values that go into the m21-4 manuals, the authoritative specifications for claims.
 */
@Schema(
    name = "benefit_claim_type",
    description =
        """
        The object used to specify a claim type. This object gives a higher level of granularity
        to claim types, especially when there are only 9 modifiers. Using this object will
        remove the need to specify EP code or label in separate fields. The values reflected
        in this object reflect the values that go into the m21-4 manuals, the authoritative
        specifications for claims.
        """)
@JsonTypeName("benefit_claim_type")
@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2023-02-04T14:34:57.376566-05:00[America/New_York]")
public class BenefitClaimType {

  @JsonProperty("name")
  private String name;

  @JsonProperty("code")
  private String code;

  @JsonProperty("description")
  private String description;

  @JsonProperty("deactiveDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime deactiveDate;

  @JsonProperty("attribute_one")
  private String attributeOne;

  @JsonProperty("attribute_one_text")
  private String attributeOneText;

  @JsonProperty("attribute_two")
  private String attributeTwo;

  @JsonProperty("attribute_two_text")
  private String attributeTwoText;

  @JsonProperty("attribute_three")
  private String attributeThree;

  @JsonProperty("attribute_three_text")
  private String attributeThreeText;

  public BenefitClaimType name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the BeneifitClaimType.
   *
   * @return name
   */
  @Schema(name = "name", description = "The name of the BeneifitClaimType.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BenefitClaimType code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code.
   *
   * @return code
   */
  @Schema(name = "code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public BenefitClaimType description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description.
   *
   * @return description
   */
  @Schema(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BenefitClaimType deactiveDate(OffsetDateTime deactiveDate) {
    this.deactiveDate = deactiveDate;
    return this;
  }

  /**
   * Date indicating when this item is deactivated and is no longer valid, or an empty value.
   *
   * @return deactiveDate
   */
  @Valid
  @Schema(
      name = "deactiveDate",
      description =
          """
          Date indicating when this item is deactivated and is no longer valid, or an
          empty value.
          """)
  public OffsetDateTime getDeactiveDate() {
    return deactiveDate;
  }

  public void setDeactiveDate(OffsetDateTime deactiveDate) {
    this.deactiveDate = deactiveDate;
  }

  public BenefitClaimType attributeOne(String attributeOne) {
    this.attributeOne = attributeOne;
    return this;
  }

  /**
   * An additional discriminator key name to give further resolution on a 3-digit code. The value is
   * typically \"CLAIM_TYPE_LABEL\".
   *
   * @return attributeOne
   */
  @Schema(
      name = "attribute_one",
      description =
          """
          An additional discriminator key name to give further resolution on a 3-digit code. The
          value is typically \"CLAIM_TYPE_LABEL\"
          """)
  public String getAttributeOne() {
    return attributeOne;
  }

  public void setAttributeOne(String attributeOne) {
    this.attributeOne = attributeOne;
  }

  public BenefitClaimType attributeOneText(String attributeOneText) {
    this.attributeOneText = attributeOneText;
    return this;
  }

  /**
   * The value of the attribute_one key. For example, it could be \"Work Item\", \"Compensation\",
   * \"Predetermination\", etc.
   *
   * @return attributeOneText
   */
  @Schema(
      name = "attribute_one_text",
      description =
          """
          The value of the attribute_one key. For example, it could be \"Work Item\",
          \"Compensation\", \"Predetermination\", etc
          """)
  public String getAttributeOneText() {
    return attributeOneText;
  }

  public void setAttributeOneText(String attributeOneText) {
    this.attributeOneText = attributeOneText;
  }

  public BenefitClaimType attributeTwo(String attributeTwo) {
    this.attributeTwo = attributeTwo;
    return this;
  }

  /**
   * A second field attribute key name. This value is typically \"USER_DISPLAY\".
   *
   * @return attributeTwo
   */
  @Schema(
      name = "attribute_two",
      description = "A second field attribute key name. This value is typically \"USER_DISPLAY\"")
  public String getAttributeTwo() {
    return attributeTwo;
  }

  public void setAttributeTwo(String attributeTwo) {
    this.attributeTwo = attributeTwo;
  }

  public BenefitClaimType attributeTwoText(String attributeTwoText) {
    this.attributeTwoText = attributeTwoText;
    return this;
  }

  /**
   * The value of the attribute_two key.
   *
   * @return attributeTwoText
   */
  @Schema(name = "attribute_two_text", description = "The value of the attribute_two key.")
  public String getAttributeTwoText() {
    return attributeTwoText;
  }

  public void setAttributeTwoText(String attributeTwoText) {
    this.attributeTwoText = attributeTwoText;
  }

  public BenefitClaimType attributeThree(String attributeThree) {
    this.attributeThree = attributeThree;
    return this;
  }

  /**
   * A third field attribute key name. It usually relates to whether the BenefitClaimType is
   * Ratings.
   *
   * @return attributeThree
   */
  @Schema(
      name = "attribute_three",
      description =
          """
          A third field attribute key name. It usually relates to whether the BenefitClaimType
          is Ratings
          """)
  public String getAttributeThree() {
    return attributeThree;
  }

  public void setAttributeThree(String attributeThree) {
    this.attributeThree = attributeThree;
  }

  public BenefitClaimType attributeThreeText(String attributeThreeText) {
    this.attributeThreeText = attributeThreeText;
    return this;
  }

  /**
   * The value of the attribute_three key.
   *
   * @return attributeThreeText
   */
  @Schema(name = "attribute_three_text", description = "The value of the attribute_three key.")
  public String getAttributeThreeText() {
    return attributeThreeText;
  }

  public void setAttributeThreeText(String attributeThreeText) {
    this.attributeThreeText = attributeThreeText;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BenefitClaimType benefitClaimType = (BenefitClaimType) o;
    return Objects.equals(this.name, benefitClaimType.name)
        && Objects.equals(this.code, benefitClaimType.code)
        && Objects.equals(this.description, benefitClaimType.description)
        && Objects.equals(this.deactiveDate, benefitClaimType.deactiveDate)
        && Objects.equals(this.attributeOne, benefitClaimType.attributeOne)
        && Objects.equals(this.attributeOneText, benefitClaimType.attributeOneText)
        && Objects.equals(this.attributeTwo, benefitClaimType.attributeTwo)
        && Objects.equals(this.attributeTwoText, benefitClaimType.attributeTwoText)
        && Objects.equals(this.attributeThree, benefitClaimType.attributeThree)
        && Objects.equals(this.attributeThreeText, benefitClaimType.attributeThreeText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        name,
        code,
        description,
        deactiveDate,
        attributeOne,
        attributeOneText,
        attributeTwo,
        attributeTwoText,
        attributeThree,
        attributeThreeText);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BenefitClaimType {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    deactiveDate: ").append(toIndentedString(deactiveDate)).append("\n");
    sb.append("    attributeOne: ").append(toIndentedString(attributeOne)).append("\n");
    sb.append("    attributeOneText: ").append(toIndentedString(attributeOneText)).append("\n");
    sb.append("    attributeTwo: ").append(toIndentedString(attributeTwo)).append("\n");
    sb.append("    attributeTwoText: ").append(toIndentedString(attributeTwoText)).append("\n");
    sb.append("    attributeThree: ").append(toIndentedString(attributeThree)).append("\n");
    sb.append("    attributeThreeText: ").append(toIndentedString(attributeThreeText)).append("\n");
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
