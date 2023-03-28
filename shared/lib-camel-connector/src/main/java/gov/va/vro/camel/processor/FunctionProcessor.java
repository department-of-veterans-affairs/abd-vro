package gov.va.vro.camel.processor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Optional;
import java.util.function.Function;

/**
 * An alternative to ".bean" component but is stable under refactoring. For example, {@code
 * bean(ClaimService.class, "process")} will break at runtime if we refactor the method name to
 * "processClaim". With FunctionProcessor you can use the real method calls, so it will be
 * refactored along with everything else.
 *
 * <p>Unlike the Bean component, the ExchangePattern and the return value of {@code
 * function.apply()} determines what is returned. The returned value will be the same as the input
 * body if:
 *
 * <ul>
 *   <li>ExchangePattern.InOnly is set
 *   <li>or ExchangePattern.InOptionalOut is set and function.apply() returns null or an Optional
 *       object that isEmpty()
 * </ul>
 *
 * <p>Usage:
 *
 * <pre>{@code
 * new FunctionProcessor<SomeInputModel, OutputEntity>(
 *   model -> yourProcessor.doSomethingGreat(model)
 * );
 * }</pre>
 *
 * or:
 *
 * <pre>{@code
 * FunctionProcessor.<SomeInputModel, OutputEntity>builder().function(
 *   model -> yourProcessor.doSomethingGreat(model)
 * ).build();
 * }</pre>
 *
 * @param <I> input type for input message body
 * @param <O> return type for output message body
 */
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class FunctionProcessor<I, O> implements Processor {
  // The expected input type. If null, no automatic conversion is done
  @Builder.Default private Class<I> inputBodyClass = null;

  // The operation to apply to the input message body
  private final Function<I, O> function;

  public static <I, O> FunctionProcessor<I, O> fromFunction(Function<I, O> function) {
    return new FunctionProcessor<>(function);
  }

  @Override
  public void process(Exchange exchange) {
    I input = getInputBody(exchange);
    O result = function.apply(input);

    conditionallySetOutputBody(exchange, result);
  }

  @SuppressWarnings("unchecked")
  private I getInputBody(Exchange exchange) {
    if (inputBodyClass == null) return (I) exchange.getIn().getBody();

    return exchange.getIn().getBody(inputBodyClass);
  }

  private void conditionallySetOutputBody(Exchange exchange, O result) {
    switch (exchange.getPattern()) {
      case InOut -> exchange.getMessage().setBody(result);
      case InOptionalOut -> {
        if (result == null || (result instanceof Optional && ((Optional) result).isEmpty())) break;
        exchange.getMessage().setBody(result);
      }
    }
  }
}
