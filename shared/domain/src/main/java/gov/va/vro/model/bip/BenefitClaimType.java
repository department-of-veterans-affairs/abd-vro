package gov.va.vro.model.bip;

import lombok.Data;

/**
 * A component in a BIP claim object.
 *
 * @author warren @Date 11/9/22
 */
@Data
public class BenefitClaimType {
  private String name;
  private String code;
  private String attribute_one;
  private String attribute_one_text;
  private String attribute_two;
  private String attribute_two_text;
  private String attribute_three;
  private String attribute_three_text;
}
