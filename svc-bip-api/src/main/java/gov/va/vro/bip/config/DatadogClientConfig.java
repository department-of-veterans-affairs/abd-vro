package gov.va.vro.bip.config;

import com.datadog.api.client.ApiClient;
import com.datadog.api.client.RetryConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Data
@Configuration
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.datadog")
@Slf4j
public class DatadogClientConfig {

  @Value("${spring.datadog.site}")
  private String site;

  @Value("${spring.datadog.api_key}")
  private String api_key;

  @Value("${spring.datadog.app_key}")
  private String app_key;

  public ApiClient getApiClient() throws Exception {
    ApiClient apiClient = null;
    try {
      if (site != null && api_key != null) {
        apiClient = new ApiClient();
        HashMap<String, String> serverVariables = new HashMap<String, String>();
        serverVariables.put("site", site);
        apiClient.setServerVariables(serverVariables);

        HashMap<String, String> secrets = new HashMap<String, String>();
        secrets.put("apiKeyAuth", api_key);
        if (app_key != null) {
          secrets.put("appKeyAuth", app_key);
        }
        apiClient.configureApiKeys(secrets);
        log.info("initialized Datadog API Client");
      } else {
        apiClient = ApiClient.getDefaultApiClient();
        log.info("initializing default Datadog default API Client");
      }
      apiClient.setRetry(new RetryConfig(true, 2, 2, 3));
    } catch (Exception e) {
      log.warn(String.format("error initializing Datadog API Client: %s", e.getMessage()));
      throw e;
    }
    return apiClient;
  }
}
