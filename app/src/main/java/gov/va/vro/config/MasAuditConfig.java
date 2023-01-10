package gov.va.vro.config;

import gov.va.vro.service.provider.MasConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configure mas queues and slack notifications. */
@Configuration
public class MasAuditConfig {

  @Bean
  MasConfig masConfig(
      @Value("${mas.processing.initial-delay}") long masProcessingInitialDelay,
      @Value("${mas.processing.subsequent-delay}") long masProcessingSubsequentDelay,
      @Value("${mas.processing.retry-count}") int masRetryCount,
      @Value("${slack.exception.channel:#{null}}") String slackExceptionChannel,
      @Value("${slack.exception.webhook:#{null}}") String slackExceptionWebhook) {
    var builder =
        MasConfig.builder()
            .masProcessingInitialDelay(masProcessingInitialDelay)
            .masProcessingSubsequentDelay(masProcessingSubsequentDelay)
            .masRetryCount(masRetryCount);
    if (slackExceptionChannel != null && slackExceptionWebhook != null) {
      builder
          .slackExceptionChannel(slackExceptionChannel)
          .slackExceptionWebhook(slackExceptionWebhook);
    }
    return builder.build();
  }
}
