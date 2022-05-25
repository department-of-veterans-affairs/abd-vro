package gov.va.vro.routes;

import gov.va.vro.AppConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/** Not for production use */
@Component
class CamelRestConfiguration extends RouteBuilder {

  @Value("${vro.context_path}")
  public String contextPath;

  @Autowired private Environment env;

  @Override
  public void configure() throws Exception {

    // servletName must match servlet given to ServletRegistrationBean in AppConfig.java
    // https://medium.com/@simon.martinelli/apache-camel-rest-endpoint-and-servlet-name-dd827a56716f
    // https://camel.apache.org/components/3.14.x/servlet-component.html#_putting_camel_jars_in_the_app_server_boot_classpath
    restConfiguration()
        .component("servlet")
        .endpointProperty("servletName", AppConfig.servletName)
        .contextPath(contextPath);
    //        System.out.println(getClass() + ": " + restConfiguration().getContextPath());

    restConfiguration()
        .bindingMode(RestBindingMode.json)
        .dataFormatProperty("prettyPrint", "true") // nice for debugging
        // .dataFormatProperty("json.in.disableFeatures",
        // "FAIL_ON_UNKNOWN_PROPERTIES,ADJUST_DATES_TO_CONTEXT_TIME_ZONE")
        // .dataFormatProperty("json.in.enableFeatures",
        // "FAIL_ON_NUMBERS_FOR_ENUMS,USE_BIG_DECIMAL_FOR_FLOATS");
        .port(env.getProperty("server.port", "8080"))
        .apiContextPath("/api-doc")
        .apiVendorExtension(true)
        .apiProperty("api.title", "JAVA DEV JOURNAL REST API")
        .apiProperty("api.version", "2.0")
        .apiProperty("cors", "true")
        .apiContextRouteId("doc-api");
  }
}
