package gov.va.vro.mocklh.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TestSpec {
  private String icn;
  private String resourceType;
  private String code;

  public String getScope() {
    return String.format("launch patient/%s.read", resourceType);
  }

  private String getBaseUrl(int port) {
    return "http://localhost:" + port;
  }

  public String getTokenUrl(int port) {
    return getBaseUrl(port) + "/token";
  }

  /**
   * Get URL
   *
   * @param port port
   * @return URL
   */
  public String getUrl(int port) {
    String baseUrl = "http://localhost:" + port;

    String url = getBaseUrl(port) + String.format("/%s?_count=100&", resourceType);
    url += "patient=" + icn;
    if (code != null) {
      url += "&code=" + code;
    }

    return url;
  }
}
