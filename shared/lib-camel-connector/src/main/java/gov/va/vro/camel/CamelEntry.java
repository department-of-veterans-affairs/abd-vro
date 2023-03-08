package gov.va.vro.camel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Used to inject messages into a Camel endpoint at the start of a workflow (or Camel route).
 *
 * <p>Intended to be used by Controller classes to initiate routing requests.
 *
 * <p>The {@code entryName} parameter corresponds to the name used for RouteBuilder's {@code from}
 * method.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CamelEntry {

  private final ProducerTemplate producerTemplate;

  /**
   * Send a message into a workflow and return the result of the workflow (synchronous).
   *
   * @param entryName Name of the entrypoint to the workflow
   * @param body The message payload to send
   * @param responseClass Class of returned object
   * @return
   */
  public <T> T inOut(String exchangeName, String entryName, Object body, Class<T> responseClass) {
    return producerTemplate.requestBody(
        toMessageQueueUri(exchangeName, entryName), body, responseClass);
  }

  /**
   * Send a message into a workflow and wait for it to finish (synchronous).
   *
   * @param entryName Name of the entrypoint to the workflow
   * @param body The message payload to send
   */
  public void inOnly(String exchangeName, String entryName, Object body) {
    producerTemplate.sendBody(toMessageQueueUri(exchangeName, entryName), body);
  }

  /**
   * Same as {@link #inOnly} except include headers on the message.
   *
   * @param headers
   */
  public void inOnly(String exchangeName, String entryName, Object body, Map headers) {
    producerTemplate.sendBodyAndHeaders(toMessageQueueUri(exchangeName, entryName), body, headers);
  }

  /** Same as {@link #inOnly} except don't wait for the workflow to finish (asynchronous). */
  public void asyncInOnly(String exchangeName, String entryName, Object body) {
    producerTemplate.asyncSendBody(toMessageQueueUri(exchangeName, entryName), body);
  }

  public static String toMessageQueueUri(String exchangeName, String routingKey) {
    return RabbitMqCamelUtils.rabbitmqProducerEndpoint(exchangeName, routingKey);
  }
}
