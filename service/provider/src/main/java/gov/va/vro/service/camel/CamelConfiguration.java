package gov.va.vro.service.camel;

import com.google.common.collect.Sets;
import com.rabbitmq.client.ConnectionFactory;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.model.Payload;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.component.seda.SedaComponent;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.impl.converter.CoreTypeConverterRegistry;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.InputStream;
import java.util.Set;

@Slf4j
@Configuration
@EnableJpaRepositories({"gov.va.vro.model", "gov.va.starter.example.persistence.model"})
@EntityScan({"gov.va.vro.model", "gov.va.starter.example.persistence.model"})
public class CamelConfiguration {
  @Autowired private CamelContext camelContext;
  @Autowired private CamelUtils camelUtils;

  @Bean
  CamelContextConfiguration contextConfiguration() {
    return new CamelContextConfiguration() {
      @Override
      public void beforeApplicationStart(CamelContext context) {
        SedaComponent sedaComponent = context.getComponent("seda", SedaComponent.class);
        sedaComponent.setDefaultBlockWhenFull(true);
      }

      @Override
      public void afterApplicationStart(CamelContext camelContext) {
        registerTypeConverter(camelContext);

        log.info(
            camelContext.getEndpoints().size()
                + " endpoints: \n\t- "
                + camelUtils.endpointsToString("\n\t- "));
        System.out.println("=====================================");
      }
    };
  }

  public static Set<Class> DTO_CLASSES = Sets.newHashSet(ClaimSubmission.class, Payload.class);

  // TODO: replace with Auto-configured TypeConverter
  // https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html#SpringBoot-Auto-configuredTypeConverter
  private void registerTypeConverter(CamelContext camelContext) {
    CamelDtoConverter.registerWith(camelContext, DTO_CLASSES);

    // printTypeConverters();
  }

  private void printTypeConverters() {
    TypeConverterRegistry registry = camelContext.getTypeConverterRegistry();
    ((CoreTypeConverterRegistry) registry)
        .getTypeMappings()
        .forEach(
            (fromClass, toClass, converter) ->
                log.debug(
                    "{} -> {} : {}",
                    fromClass.getName(),
                    toClass.getName(),
                    converter.getClass().getSimpleName()));
    log.debug(
        "ClaimSubmission -> byte[] : " + registry.lookup(ClaimSubmission.class, byte[].class));
    log.debug(
        "byte[] -> ClaimSubmission : " + registry.lookup(byte[].class, ClaimSubmission.class));
    log.debug(
        "ClaimSubmission -> InputStream : "
            + registry.lookup(ClaimSubmission.class, InputStream.class));
  }

  @Autowired public MessageQueueProperties messageQueueProps;

  @Bean
  ConnectionFactory rabbitmqConnectionFactory() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(messageQueueProps.getHost());
    factory.setPort(messageQueueProps.getPort());
    factory.setUsername(messageQueueProps.getUser());
    factory.setPassword(messageQueueProps.getPassword());
    return factory;
  }

  @Value("${vro.context_path}")
  public String contextPath;

  public static String servletName = "VroCamelRestServlet";

  // https://opensource.com/article/18/9/camel-rest-dsl
  // https://stackoverflow.com/questions/55127006/multiple-servlets-with-camel-servlet-possible
  @Bean
  @ConditionalOnProperty(
      value = "vro.camel_rest_api.enable",
      havingValue = "true",
      matchIfMissing = false)
  ServletRegistrationBean servletRegistrationBean() {
    ServletRegistrationBean servlet =
        new ServletRegistrationBean(new CamelHttpTransportServlet(), contextPath + "/*");
    servlet.setName(servletName);
    log.info("Camel REST servlet: {}", servlet.toString());
    return servlet;
  }
}
