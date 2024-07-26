package gov.va.vro.camel.processor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Used for taking the input body of type I, preparing and sending a request body to another Camel
 * route (via `requestUri`), then merging the response with the input body into an output of type I.
 *
 * <p>This allows the request and response to and from the other route to be entirely different from
 * the input body while retaining the input body.
 *
 * <p>Headers on the input message are also sent to the requestUri.
 *
 * <p>The ExchangePattern determines what is returned. The returned value will be the same as the
 * input body if:
 *
 * <ul>
 *   <li>ExchangePattern.InOnly is set
 *   <li>or ExchangePattern.InOptionalOut is set and {@code function.apply()} returns null or an
 *       Optional object that isEmpty()
 * </ul>
 *
 * (Note: if using ExchangePattern.InOnly and the input body is modified by the other route, then
 * the output body will reflect those modifications (since they are the same object.)
 *
 * @param <I> class of input (and output) message body
 * @param <REQ> class of request to request endpoint
 * @param <RESP> class of response from request endpoint
 */

// builderMethodName is needed to work around method name clash compile error
// see https://github.com/projectlombok/lombok/issues/2524#issuecomment-662838468
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
public class RequestAndMerge<I, REQ, RESP> implements Processor {
  ProducerTemplate producer;

  public static <I, REQ, RESP> RequestAndMergeBuilder<I, REQ, RESP, ?, ?> factory(
      ProducerTemplate producer) {
    return RequestAndMerge.<I, REQ, RESP>builder().producer(producer);
  }

  // Camel URI to send request
  String requestUri;

  // The expected input type to the requestUri. If null, no automatic conversion is done
  @Builder.Default Class<I> inputBodyClass = null;

  // The operation to apply to the input message body
  Function<I, REQ> prepareRequest;

  // The expected response type from the requestUri. If null, no automatic conversion is done
  @Builder.Default Class<RESP> responseClass = null;

  // default is to ignore the response
  @Builder.Default BiFunction<I, RESP, I> mergeResponse = (input, response) -> input;

  ProcessorUtils processorUtils;

  /** When using this, no automatic conversion is done with the input message body. */
  public static <I, REQ, RESP> RequestAndMerge<I, REQ, RESP> build(
      String requestUri, Function<I, REQ> prepareRequest, BiFunction<I, RESP, I> mergeResponse) {
    return RequestAndMerge.<I, REQ, RESP>builder()
        .requestUri(requestUri)
        .prepareRequest(prepareRequest)
        .mergeResponse(mergeResponse)
        .processorUtils(new ProcessorUtils())
        .build();
  }

  public void process(Exchange exchange) {
    I input = processorUtils.getInputBody(exchange, inputBodyClass);
    REQ request = prepareRequest.apply(input);
    RESP response = makeRequest(request, exchange.getMessage().getHeaders());
    I mergedBody = mergeResponse.apply(input, response);

    processorUtils.conditionallySetOutputBody(exchange, mergedBody);
  }

  @SuppressWarnings("unchecked")
  RESP makeRequest(REQ request, Map<String, Object> headers) {
    if (responseClass == null)
      return (RESP) producer.requestBodyAndHeaders(requestUri, request, headers);

    return producer.requestBodyAndHeaders(requestUri, request, headers, responseClass);
  }
}
