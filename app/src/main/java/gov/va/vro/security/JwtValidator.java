package gov.va.vro.security;

import static java.util.Objects.isNull;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gov.va.vro.config.LhApiProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtValidator {

  private static final String BEARER = "Bearer ";
  private static final String IS_TOKEN_VALIDATED = "validated_token";

  private static final String VALIDATE_TOKEN = "yes";

  /**
   * Sub string bearer.
   *
   * @param authorizationHdr authorization hdr.
   * @return sub string bearer.
   */
  public String subStringBearer(String authorizationHdr) {
    try {
      return authorizationHdr.substring(BEARER.length());
    } catch (Exception ex) {
      throw new InvalidTokenException("There is no AccessToken in a request header");
    }
  }

  /**
   * Decodes the jwt token.
   *
   * @param jwtToken jwt token.
   * @return decoded jwt.
   */
  public DecodedJWT decodeToken(String jwtToken) {
    if (isNull(jwtToken)) {
      throw new InvalidTokenException("Token has not been provided");
    }
    try {
      DecodedJWT decodedJwt = JWT.decode(jwtToken);
      log.info("Token decoded successfully");
      return decodedJwt;
    } catch (RuntimeException exception) {
      throw new InvalidTokenException(
          "Invalid JWT or JSON format of each of the jwt parts", exception);
    }
  }

  /**
   * Verifies JWT token.
   *
   * @param decodedJwt decoded jwt.
   */
  public void verifyTokenHeader(DecodedJWT decodedJwt) {
    if (isNull(decodedJwt.getHeader())) {
      throw new InvalidTokenException("Invalid token, Header missing");
    }
    if (isNull((decodedJwt.getAlgorithm())) || isNull((decodedJwt.getKeyId()))) {
      throw new InvalidTokenException("Invalid token, should have alg, kid claims in the header");
    } else {
      log.info("Token's header is valid");
    }
  }

  /**
   * Verifies the payload.
   *
   * @param decodedJwt decoded jwt.
   */
  public void verifyPayload(DecodedJWT decodedJwt, String methodName) {
    JsonObject payloadAsJson = decodeTokenPayloadToJsonObject(decodedJwt);
    if (hasTokenExpired(payloadAsJson)) {
      throw new InvalidTokenException("Token has expired");
    }
    log.info("Token has not expired");

    Boolean validScope = false;
    if (VALIDATE_TOKEN.equalsIgnoreCase(lhApiProps.getValidateToken())) {
      if (payloadAsJson.has("scp")) {
        for (JsonElement scope : payloadAsJson.getAsJsonArray("scp")) {
          log.info("Scope : " + scope);
          if (scope.getAsString().toLowerCase().contains(methodName.toLowerCase())) {
            validScope = true;
          }
        }
      }
      if (!validScope) {
        throw new InvalidTokenException("Token doesn't contain valid scope information");
      }
    }
    log.info("Token's payload contain scope information");
  }

  /**
   * Decodes JWT into a JSON object.
   *
   * @param decodedJwt decoded JWT.
   * @return JsonObject decode token payload.
   */
  public JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJwt) {
    try {
      String payloadAsString = decodedJwt.getPayload();
      return new Gson()
          .fromJson(
              new String(Base64.getDecoder().decode(payloadAsString), StandardCharsets.UTF_8),
              JsonObject.class);
    } catch (RuntimeException exception) {
      throw new InvalidTokenException(
          "Invalid JWT or JSON format of each of the jwt parts", exception);
    }
  }

  /**
   * Checks if token has expired.
   *
   * @param payloadAsJson payload.
   * @return true or false if token has expired.
   */
  public boolean hasTokenExpired(JsonObject payloadAsJson) {
    Instant expirationDatetime = extractExpirationDate(payloadAsJson);
    return Instant.now().isAfter(expirationDatetime);
  }

  /**
   * Gets the expiration date.
   *
   * @param payloadAsJson payload
   * @return expiration date.
   */
  public Instant extractExpirationDate(JsonObject payloadAsJson) {
    try {
      return Instant.ofEpochSecond(payloadAsJson.get("exp").getAsLong());
    } catch (NullPointerException ex) {
      throw new InvalidTokenException("There is no 'exp' claim in the token payload");
    }
  }

  /**
   * Validates token using LH.
   *
   * @param jwtToken JWT token.
   * @return true or false.
   */
  public boolean validateTokenUsingLh(String jwtToken) {
    if (VALIDATE_TOKEN.equalsIgnoreCase(lhApiProps.getValidateToken())) {
      try {
        HttpHeaders lhHttpHeaders = new HttpHeaders();
        lhHttpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        lhHttpHeaders.add("apikey", lhApiProps.getApiKey());
        lhHttpHeaders.add("Authorization", "Bearer " + jwtToken);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("aud", lhApiProps.getVroAudUrl());

        HttpEntity formEntity = new HttpEntity<>(requestBody, lhHttpHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response =
            restTemplate.exchange(
                lhApiProps.getTokenValidatorUrl(), HttpMethod.POST, formEntity, String.class);
        JsonNode lhResp = new ObjectMapper().readTree(String.valueOf(response.getBody()));
        // Sample response
        // https://department-of-veterans-affairs.github.io/
        // lighthouse-api-standards/security/oauth/token-validation/#self-verification
        if ((!isNull(lhResp.get("data")))
            && (!isNull(lhResp.get("data").get("type")))
            && IS_TOKEN_VALIDATED.equals(lhResp.get("data").get("type").asText().toLowerCase())) {
          // NOP
        } else {
          log.error("Could not validate token against LightHouse API.");
          throw new InvalidTokenException("Could not validate token against LightHouse API");
        }
      } catch (RestClientException | JsonProcessingException e) {
        log.error("Could not validate token against LightHouse API.", e);
        throw new InvalidTokenException("Could not validate token against LightHouse API");
      }
    }
    return true;
  }
}
