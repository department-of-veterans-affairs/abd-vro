package gov.va.vro.mockbipclaims.util;

import gov.va.vro.mockbipclaims.config.JwtTestProps;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtGenerator {
  private final JwtTestProps props;

  /**
   * Henerates the JWT.
   *
   * @return The JWT
   */
  public String generate() {
    return Jwts.builder()
        .setHeaderParam("typ", "JWT")
        .setClaims(props)
        .signWith(props.getSignatureAlgorithm(), props.getSigningKey())
        .compact();
  }
}
