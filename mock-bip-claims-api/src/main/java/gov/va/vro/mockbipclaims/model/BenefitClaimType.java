package gov.va.vro.mockbipclaims.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

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
@Data
public class BenefitClaimType {
  private String name;

  private String code;

  private String description;

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
}
