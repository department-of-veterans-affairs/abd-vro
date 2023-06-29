package gov.va.vro.camel.processor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.function.Function;

/**
 * An alternative to ".bean" Camel component but is stable under refactoring. For example, {@code
 * bean(ClaimService.class, "process")} will break at runtime if we refactor the method name to
 * "processClaim". With FunctionProcessor you can use the real method calls, so it will be
 * refactored along with everything else.
 *
 * <p>Unlike the Bean component, the ExchangePattern determines what is returned. The returned value
 * will be the same as the input body if:
 *
 * <ul>
 *   <li>ExchangePattern.InOnly is set
 *   <li>or ExchangePattern.InOptionalOut is set and {@code function.apply()} returns null or an
 *       Optional object that isEmpty()
 * </ul>
 *
 * <p>Usage examples:
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
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class FunctionProcessor<I, O> implements Processor {
  // The expected input type. If null, no automatic conversion is done
  @Builder.Default Class<I> inputBodyClass = null;

  // The operation to apply to the input message body
  Function<I, O> function;

  ProcessorUtils processorUtils;

  /** When using this, no automatic conversion is done with the input message body. */
  public static <I, O> FunctionProcessor<I, O> fromFunction(Function<I, O> function) {
    return FunctionProcessor.<I, O>builder()
        .function(function)
        .processorUtils(new ProcessorUtils())
        .build();
  }

  public static <I, O> FunctionProcessor<I, O> fromFunction(
      Class<I> inputBodyClass, Function<I, O> function) {
    return FunctionProcessor.<I, O>builder()
        .function(function)
        .inputBodyClass(inputBodyClass)
        .processorUtils(new ProcessorUtils())
        .build();
  }

  @Override
  public void process(Exchange exchange) {
    I input = processorUtils.getInputBody(exchange, inputBodyClass);
    // In case of ClassCastException here, did you set inputBodyClass?
    O result = function.apply(input);

    processorUtils.conditionallySetOutputBody(exchange, result);
  }
}
