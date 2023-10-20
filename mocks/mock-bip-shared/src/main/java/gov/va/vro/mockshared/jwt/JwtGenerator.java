package gov.va.vro.mockshared.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtGenerator {
  private final JwtSpecification props;

  /**
   * Generates the JWT.
   *
   * @return The JWT
   */
  public String generate() {
    return Jwts.builder()
        .setHeaderParam("typ", "JWT")
        .setClaims(props)
        .signWith(props.getSigningKey(), props.getSignatureAlgorithm())
        .compact();
  }
}
