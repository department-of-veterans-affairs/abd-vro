package gov.va.vro.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;

import static java.util.Objects.isNull;

@Slf4j
public class JwtValidator {

  public static final String BEARER = "Bearer ";

  private DecodedJWT decodeToken(String jwtToken) {
    if (isNull(jwtToken)){
      throw new InvalidTokenException("Token has not been provided");
    }
    DecodedJWT decodedJWT = JWT.decode(jwtToken);
    log.info("Token decoded successfully");
    return decodedJWT;
  }

  private void verifyTokenHeader(DecodedJWT decodedJWT) {
      if (decodedJWT.getType().equals("JWT")) {
        log.info("Token's header is correct");
      } else {
        throw new InvalidTokenException("Token is not JWT type");
      }
  }

  private void verifyPayload(DecodedJWT decodedJWT) {
    JsonObject payloadAsJson = decodeTokenPayloadToJsonObject(decodedJWT);
    if (hasTokenExpired(payloadAsJson)) {
      throw new InvalidTokenException("Token has expired");
    }
    log.debug("Token has not expired");

    if (!hasTokenRealmRolesClaim(payloadAsJson)) {
      throw new InvalidTokenException("Token doesn't contain claims with realm roles");
    }
    log.debug("Token's payload contain claims with realm roles");

    if (!hasTokenScopeInfo(payloadAsJson)) {
      throw new InvalidTokenException("Token doesn't contain scope information");
    }
    log.debug("Token's payload contain scope information");
  }

  private JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJWT) {
    try {
      String payloadAsString = decodedJWT.getPayload();
      return new Gson().fromJson(
              new String(Base64.getDecoder().decode(payloadAsString), StandardCharsets.UTF_8),
              JsonObject.class);
    }   catch (RuntimeException exception){
      throw new InvalidTokenException("Invalid JWT or JSON format of each of the jwt parts", exception);
    }
  }

  private boolean hasTokenExpired(JsonObject payloadAsJson) {
    Instant expirationDatetime = extractExpirationDate(payloadAsJson);
    return Instant.now().isAfter(expirationDatetime);
  }

  private Instant extractExpirationDate(JsonObject payloadAsJson) {
    try {
      return Instant.ofEpochSecond(payloadAsJson.get("exp").getAsLong());
    } catch (NullPointerException ex) {
      throw new InvalidTokenException("There is no 'exp' claim in the token payload");
    }
  }

  private boolean hasTokenRealmRolesClaim(JsonObject payloadAsJson) {
    try {
      return payloadAsJson.getAsJsonObject("realm_access").getAsJsonArray("roles").size() > 0;
    } catch (NullPointerException ex) {
      return false;
    }
  }

  private boolean hasTokenScopeInfo(JsonObject payloadAsJson) {
    return payloadAsJson.has("scope");
  }

  private String subStringBearer(String authorizationHeader) {
    try {
      return authorizationHeader.substring(BEARER.length());
    } catch (Exception ex) {
      throw new InvalidTokenException("There is no AccessToken in a request header");
    }
  }
}
