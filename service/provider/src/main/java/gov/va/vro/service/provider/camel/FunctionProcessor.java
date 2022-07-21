package gov.va.vro.service.provider.camel;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.function.Function;

/**
 * An alternative to "bean", which is stable under refactoring.
 *
 * <p>For example, bean(ClaimService.class, "process") will break at runtime if we refactor the
 * method name to "processClaim".
 *
 * <p>With FunctionProcessor you can use the real method calls, so it will be refactored along with
 * everything else.
 *
 * @param <I>
 * @param <O>
 */
@RequiredArgsConstructor
public class FunctionProcessor<I, O> implements Processor {

  private final Function<I, O> function;

  @Override
  public void process(Exchange exchange) {
    I input = (I) exchange.getIn().getBody();
    O result = function.apply(input);
    exchange.getMessage().setBody(result);
  }

  public static <I, O> FunctionProcessor<I, O> fromFunction(Function<I, O> function) {
    return new FunctionProcessor<>(function);
  }
}
