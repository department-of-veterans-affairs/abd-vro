package gov.va.vro.service.provider.camel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.rabbitmq.client.ConnectionFactory;
import gov.va.starter.example.service.spi.claimsubmission.model.ClaimSubmission;
import gov.va.vro.persistence.model.PayloadEntity;
import gov.va.vro.service.spi.demo.model.AssessHealthData;
import gov.va.vro.service.spi.demo.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.component.seda.SedaComponent;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.impl.converter.CoreTypeConverterRegistry;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Set;

@Slf4j
@Configuration
@EnableJpaRepositories({"gov.va.vro.model", "gov.va.starter.example.persistence.model"})
@EntityScan({"gov.va.vro.model", "gov.va.starter.example.persistence.model"})
@RequiredArgsConstructor
public class CamelConfiguration {
  private final CamelContext camelContext;
  private final CamelUtils camelUtils;

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
        registerTypeConverters();

        log.info(
            camelContext.getEndpoints().size()
                + " endpoints: \n\t- "
                + camelUtils.endpointsToString("\n\t- "));
        log.info("\n=====================================");
      }
    };
  }

  private static final Set<Class> dtoClasses =
      Sets.newHashSet(
          ClaimSubmission.class,
          PayloadEntity.class,
          AssessHealthData.class,
          GeneratePdfPayload.class);
  private final ObjectMapper mapper;

  // TODO: replace with Auto-configured TypeConverter
  // https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html#SpringBoot-Auto-configuredTypeConverter
  @Bean
  CamelDtoConverter registerTypeConverters() {
    CamelDtoConverter converter = new CamelDtoConverter(dtoClasses, mapper);

    TypeConverterRegistry registry = camelContext.getTypeConverterRegistry();
    // registry.setTypeConverterExists(TypeConverterExists.Override);
    dtoClasses.forEach(
        clazz -> {
          registry.addTypeConverter(clazz, byte[].class, converter);
          registry.addTypeConverter(byte[].class, clazz, converter);

          // registry.addTypeConverter(clazz, InputStream.class, converter);
          // registry.addTypeConverter(InputStream.class, clazz, dtoConverter);
        });

    // printTypeConverters();
    return converter;
  }

  private void printTypeConverters() {
    TypeConverterRegistry registry = camelContext.getTypeConverterRegistry();
    ((CoreTypeConverterRegistry) registry)
        .getTypeMappings()
        .forEach(
            (fromClass, toClass, converter) ->
                log.info(
                    "{} -> {} : {}",
                    fromClass.getName(),
                    toClass.getName(),
                    converter.getClass().getSimpleName()));
    log.info(
        "AssessHealthData -> byte[] : " + registry.lookup(AssessHealthData.class, byte[].class));
    log.info(
        "byte[] -> AssessHealthData : " + registry.lookup(byte[].class, AssessHealthData.class));
  }

  private final MessageQueueProperties messageQueueProps;

  @Bean
  ConnectionFactory rabbitmqConnectionFactory() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(messageQueueProps.getHost());
    factory.setPort(messageQueueProps.getPort());
    factory.setUsername(messageQueueProps.getUser());
    factory.setPassword(messageQueueProps.getPassword());
    return factory;
  }

  public static String servletName = "VroCamelRestServlet";

  // https://opensource.com/article/18/9/camel-rest-dsl
  // https://stackoverflow.com/questions/55127006/multiple-servlets-with-camel-servlet-possible
  @Bean
  @ConditionalOnProperty(
      value = "vro.camel_rest_api.enable",
      havingValue = "true",
      matchIfMissing = false)
  ServletRegistrationBean servletRegistrationBean(
      @Value("${vro.context_path}") String contextPath) {
    ServletRegistrationBean servlet =
        new ServletRegistrationBean(new CamelHttpTransportServlet(), contextPath + "/*");
    servlet.setName(servletName);
    log.info("Camel REST servlet: {}", servlet.toString());
    return servlet;
  }
}
