package gov.va.vro.service.provider.camel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.rabbitmq.client.ConnectionFactory;
import gov.va.vro.model.mas.MasAutomatedClaimPayload;
import gov.va.vro.service.spi.model.Claim;
import gov.va.vro.service.spi.model.GeneratePdfPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.component.seda.SedaComponent;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Set;

@Slf4j
@Configuration
@EnableJpaRepositories({"gov.va.vro.model"})
@EntityScan({"gov.va.vro.model"})
@RequiredArgsConstructor
public class CamelConfiguration {
  private final CamelContext camelContext;
  private final CamelUtils camelUtils;

  private final MessageQueueProperties messageQueueProps;

  private static final Set<Class> dtoClasses =
      Sets.newHashSet(Claim.class, GeneratePdfPayload.class, MasAutomatedClaimPayload.class);
  private final ObjectMapper mapper;

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

  // TODO: replace with Auto-configured TypeConverter
  // https://camel.apache.org/camel-spring-boot/3.11.x/spring-boot.html#SpringBoot-Auto-configuredTypeConverter
  @Bean
  CamelDtoConverter registerTypeConverters() {
    CamelDtoConverter converter = new CamelDtoConverter(dtoClasses, mapper);

    TypeConverterRegistry registry = camelContext.getTypeConverterRegistry();
    dtoClasses.forEach(
        clazz -> {
          registry.addTypeConverter(clazz, byte[].class, converter);
          registry.addTypeConverter(byte[].class, clazz, converter);
        });

    return converter;
  }

  @Bean
  ConnectionFactory rabbitmqConnectionFactory() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(messageQueueProps.getHost());
    factory.setPort(messageQueueProps.getPort());
    factory.setUsername(messageQueueProps.getUser());
    factory.setPassword(messageQueueProps.getPassword());
    return factory;
  }
}
