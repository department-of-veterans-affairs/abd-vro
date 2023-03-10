package gov.va.vro.mockmas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasTokenRequest {
  private String scope;

  @JsonProperty("grant-type")
  private String grantType;

  @JsonProperty("client-id")
  private String clientId;

  @JsonProperty("client_secret")
  private String clientSecret;
}
