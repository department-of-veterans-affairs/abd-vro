package gov.va.vro.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LhApiProps {

  private final String tokenValidatorURL;
  private final String vroAudURL;
  private final String apiKey;
  private final String validateToken;
}
