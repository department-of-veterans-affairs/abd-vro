package gov.va.vro.mockshared.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtGenerator {
  private final JwtSpecification props;

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
