package gov.va.vro.mocklh.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LhToken {
  private String accessToken;
  private String tokenType;
  private String scope;
  private int expiresIn;
  private String state;
  private String patient;
}
