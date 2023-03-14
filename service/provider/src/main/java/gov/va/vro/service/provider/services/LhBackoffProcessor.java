package gov.va.vro.service.provider.services;

import gov.va.vro.service.provider.ExternalCallException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LhBackoffProcessor implements Processor {

  private static final int MAX_RETRIES = 5;
  private static final int INITIAL_DELAY = 1000;
  private static final int MAX_DELAY = 60000;

  private int retries = 0;

  @Override
  @SneakyThrows
  public void process(Exchange exchange) {
    if (retries >= MAX_RETRIES) {
      throw new ExternalCallException("Max retries exceeded");
    }
    int delay = Math.min(MAX_DELAY, INITIAL_DELAY * (int) Math.pow(2, retries++));
    Thread.sleep(delay);
  }
}
