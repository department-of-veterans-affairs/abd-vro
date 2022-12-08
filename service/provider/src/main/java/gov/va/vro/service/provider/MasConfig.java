package gov.va.vro.service.provider;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MasConfig {

  private long masProcessingInitialDelay;

  private long masProcessingSubsequentDelay;

  private int masRetryCount;

  private String slackExceptionChannel;

  private String slackExceptionWebhook;
}
