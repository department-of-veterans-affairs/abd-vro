package gov.va.vro.model.bip;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * A component in a BIP claim object.
 *
 * @author warren @Date 11/9/22
 */
@RequiredArgsConstructor
@Data
public class BenefitClaimType {
  private String name;
  private String code;
  private String attributeOne;
  private String attributeOneText;
  private String attributeTwo;
  private String attributeTwoText;
  private String attributeThree;
  private String attributeThreeText;
}
