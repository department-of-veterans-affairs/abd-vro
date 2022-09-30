package gov.va.vro.abddataaccess.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Lighthouse token message.
 *
 * @author Warren Lin
 */
@Setter
@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LighthouseTokenMessage {
  private String accessToken;
  private String tokenType;
  private String scope;
  private int expiresIn;
  private String state;
  private String patient;
}
