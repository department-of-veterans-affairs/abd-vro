package gov.va.vro.mockshared;

public interface JwtAppConfig {
  String getSubject();

  String getUserId();

  String getSecret();

  String getIssuer();

  String getStationId();

  String getApplicationId();

  int getExpirationSeconds();
}
