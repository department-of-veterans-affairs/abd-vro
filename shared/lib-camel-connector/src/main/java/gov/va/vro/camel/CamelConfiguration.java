package gov.va.vro.camel;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.component.seda.SedaComponent;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CamelConfiguration {
  private final CamelContext camelContext;
  private final CamelUtils camelUtils;
  private final CamelDtoClassesProperties camelDtoClassesProperties;

  @Bean
  CamelContextConfiguration contextConfiguration(ObjectMapper mapper) {
    return new CamelContextConfiguration() {
      @Override
      public void beforeApplicationStart(CamelContext context) {
        SedaComponent sedaComponent = context.getComponent("seda", SedaComponent.class);
        sedaComponent.setDefaultBlockWhenFull(true);
      }

      @Override
      public void afterApplicationStart(CamelContext camelContext) {
        try {
          List<Class> dtoClasses = camelDtoClassesProperties.getActualDtoClasses();
          registerTypeConverters(dtoClasses, mapper);
        } catch (IOException e) {
          log.error("Check the vro.camel.dto-classes property", e);
        }

        log.info(
            camelContext.getEndpoints().size()
                + " endpoints: \n\t- "
                + camelUtils.endpointsToString("\n\t- "));
        log.info("Application started\n=====================================");
      }
    };
  }

  void registerTypeConverters(Collection<Class> dtoClasses, ObjectMapper mapper) {
    CamelDtoConverter dtoConverter = new CamelDtoConverter(dtoClasses, mapper);

    TypeConverterRegistry registry = camelContext.getTypeConverterRegistry();
    dtoClasses.forEach(
        clazz -> {
          log.info("Registering CamelDtoConverter for: {}", clazz);
          registry.addTypeConverter(clazz, byte[].class, dtoConverter);
          registry.addTypeConverter(byte[].class, clazz, dtoConverter);
        });
  }
}
