package gov.va.vro.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LhApiProps {

  private final String tokenValidatorUrl;
  private final String vroAudUrl;
  private final String apiKey;
  private final String validateToken;
}
