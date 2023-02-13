package gov.va.vro.mockshared.jwt;

public interface JwtAppConfig {
  String getSubject();

  String getUserId();

  String getSecret();

  String getIssuer();

  String getStationId();

  String getApplicationId();

  int getExpirationSeconds();
}
