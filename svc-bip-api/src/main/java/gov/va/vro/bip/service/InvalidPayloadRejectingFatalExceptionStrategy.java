package gov.va.vro.bip.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.listener.adapter.ReplyFailureException;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InvalidPayloadRejectingFatalExceptionStrategy implements FatalExceptionStrategy {

  private final MetricLoggerService metricLoggerService = new MetricLoggerService();

  @Override
  public boolean isFatal(@NotNull Throwable t) {
    if (t instanceof ListenerExecutionFailedException
        && (t.getCause() instanceof MessageConversionException
            || t.getCause() instanceof MethodArgumentNotValidException
            || t.getCause() instanceof ReplyFailureException)) {
      log.warn(
          "Fatal message conversion error; message rejected; it will be dropped: {}",
          ((ListenerExecutionFailedException) t).getFailedMessage());

      metricLoggerService.submitCount(
          MetricLoggerService.METRIC.MESSAGE_CONVERSION_ERROR,
          new String[] {
            "event:fatalMessageConversionError",
            "source:InvalidPayloadRejectingFatalExceptionStrategy",
            String.format("error:%s", ((ListenerExecutionFailedException) t).getFailedMessage())
          });
      return true;
    }
    return false;
  }
}
