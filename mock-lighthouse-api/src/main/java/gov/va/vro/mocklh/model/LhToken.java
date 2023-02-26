package gov.va.vro.mocklh.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LhToken {
  private String accessToken;
  private String tokenType;
  private String scope;
  private int expiresIn;
  private String state;
  private String patient;
}
