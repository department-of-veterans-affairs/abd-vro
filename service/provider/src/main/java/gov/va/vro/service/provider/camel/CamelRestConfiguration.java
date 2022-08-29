package gov.va.vro.service.provider.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Not for production. For quick development and testing of an API in dev. Don't have to implement
 * API, controller, and request-response mapper classes.
 */
@Component
@ConditionalOnProperty(value = "vro.camel_rest_api.enable", havingValue = "true")
class CamelRestConfiguration extends RouteBuilder {
  private final String contextPath;
  private final Environment env;

  CamelRestConfiguration(@Value("${vro.context_path}") String contextPath, Environment env) {
    this.contextPath = contextPath;
    this.env = env;
  }

  @Override
  public void configure() {
    // servletName must match servlet given to ServletRegistrationBean in AppConfig.java
    // https://medium.com/@simon.martinelli/apache-camel-rest-endpoint-and-servlet-name-dd827a56716f
    // https://camel.apache.org/components/3.14.x/servlet-component.html#_putting_camel_jars_in_the_app_server_boot_classpath
    restConfiguration()
        .component("servlet")
        .endpointProperty("servletName", CamelConfiguration.servletName)
        .contextPath(contextPath)
        .bindingMode(RestBindingMode.json)
        .dataFormatProperty("prettyPrint", "true") // useful for debugging
        // .dataFormatProperty("json.in.disableFeatures",
        // "FAIL_ON_UNKNOWN_PROPERTIES,ADJUST_DATES_TO_CONTEXT_TIME_ZONE")
        // .dataFormatProperty("json.in.enableFeatures",
        // "FAIL_ON_NUMBERS_FOR_ENUMS,USE_BIG_DECIMAL_FOR_FLOATS");
        .apiContextRouteId("doc-api")
        .apiContextPath("/api-doc")
        .apiVendorExtension(true)
        .apiProperty("api.title", "VRO Camel REST API")
        .apiProperty("cors", "true");

    String port = env.getProperty("server.port");
    log.info(
        "=== VRO === Enabled Camel REST API at http://localhost:"
            + port
            + restConfiguration().getContextPath()
            + "  Swagger spec at subpath "
            + restConfiguration().getApiContextPath());
  }
}
