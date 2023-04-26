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
        toMqProducerUri(exchangeName, entryName), body, responseClass);
  }

  /**
   * Send a message into a workflow and wait for it to finish (synchronous).
   *
   * @param entryName Name of the entrypoint to the workflow
   * @param body The message payload to send
   */
  public void inOnly(String exchangeName, String entryName, Object body) {
    producerTemplate.sendBody(toMqProducerUri(exchangeName, entryName), body);
  }

  /**
   * Same as {@link #inOnly} except include headers on the message.
   *
   * <p>From https://examples.javacodegeeks.com/apache-camel-headers-vs-properties-example: "camel’s
   * exchange headers are not for custom data exchange (even though it is possible for us to use
   * them in that way) but usually for protocol-related parameters" Also see
   * https://stackoverflow.com/a/50860718
   *
   * <p>Unfortunately Exchange properties are not preserved over RabbitMQ -- see
   * https://camel.apache.org/components/3.19.x/rabbitmq-component.html so the options are to use
   * headers (which are preserved over RabbitMQ) or move the header/properties to be part of the
   * message body.
   *
   * @param headers
   */
  public void inOnly(
      String exchangeName, String entryName, Object body, Map<String, Object> headers) {
    producerTemplate.sendBodyAndHeaders(toMqProducerUri(exchangeName, entryName), body, headers);
  }

  /** Same as {@link #inOnly} except don't wait for the workflow to finish (asynchronous). */
  public void asyncInOnly(String exchangeName, String entryName, Object body) {
    producerTemplate.asyncSendBody(toMqProducerUri(exchangeName, entryName), body);
  }

  private static String toMqProducerUri(String exchangeName, String routingKey) {
    return ToRabbitMqRouteHelper.rabbitmqProducerEndpoint(exchangeName, routingKey);
  }
}
