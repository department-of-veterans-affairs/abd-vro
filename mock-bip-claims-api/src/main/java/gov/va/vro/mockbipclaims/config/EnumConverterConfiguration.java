package gov.va.vro.mockbipclaims.config;

import gov.va.vro.mockbipclaims.model.PhaseType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

  @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.phaseTypeConverter")
  Converter<String, PhaseType> phaseTypeConverter() {
    return new Converter<String, PhaseType>() {
      @Override
      public PhaseType convert(String source) {
        return PhaseType.fromValue(source);
      }
    };
  }
}
