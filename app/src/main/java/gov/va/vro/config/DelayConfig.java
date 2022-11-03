package gov.va.vro.config;

import gov.va.vro.service.provider.MasDelays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configure queue delays. */
@Configuration
public class DelayConfig {

  @Bean
  MasDelays masDelays(
      @Value("${mas-processing-initial-delay}") long masProcessingInitialDelay,
      @Value("${mas-processing-subsequent-delay}") long masProcessingSubsequentDelay,
      @Value("${mas-processing-retry-count}") int masRetryCount) {
    return new MasDelays(masProcessingInitialDelay, masProcessingSubsequentDelay, masRetryCount);
  }
}
