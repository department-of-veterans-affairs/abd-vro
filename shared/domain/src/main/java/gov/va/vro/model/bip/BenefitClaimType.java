package gov.va.vro.model.bip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * A component in a BIP claim object.
 *
 * @author warren @Date 11/9/22
 */
@RequiredArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenefitClaimType {
  private String name;
  private String code;

  @JsonProperty("description")
  private String description;

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
}
