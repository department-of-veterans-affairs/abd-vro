package gov.va.vro.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;

import java.util.function.BiFunction;

@Slf4j
public class OnExceptionHelper {

  public static <T extends Throwable> OnExceptionDefinition catchExceptionsFor(
      RouteBuilder builder, Class<T> catchClass) {
    return catchExceptionsFor(builder, catchClass, true, null);
  }

  public static <T extends Throwable> OnExceptionDefinition catchExceptionsFor(
      RouteBuilder builder, Class<T> catchClass, boolean verbose) {
    return catchExceptionsFor(builder, catchClass, verbose, null);
  }

  /**
   * Create an onException route to handle exceptions that may happen along the route.
   *
   * @param builder RouteBuilder in which exceptions are being caught
   * @param catchClass Exception class to catch
   * @param verbose if true, logs more info for debugging
   * @param exceptionHandler the custom function that will return a valid object with indicators
   *     that an error has occurred
   * @param <T> catchClass
   * @param <O> output class of exceptionHandler
   * @return a OnExceptionDefinition that can be appended on
   */
  public static <T extends Throwable, O> OnExceptionDefinition catchExceptionsFor(
      RouteBuilder builder,
      Class<T> catchClass,
      boolean verbose,
      BiFunction<Exchange, T, O> exceptionHandler) {
    // On any Exception during any route, return an error message so that the requesting
    // Controller isn't waiting for response and throws an uninformative ExchangeTimedOutException
    var onExceptionDef =
        builder
            .onException(catchClass)
            // In case the ExchangePattern is different when an exception is thrown, set to InOut so
            // that the original route can return an error message back to the caller (like an API
            // Controller)
            .setExchangePattern(ExchangePattern.InOut)
            .log(LoggingLevel.ERROR, "Caught throwable! msg body: ${body}")

            // Indicate that the exception is handled; otherwise response is not sent
            // See https://camel.apache.org/manual/exception-clause.html for details
            .handled(true);

    if (verbose) {
      onExceptionDef
          .log("exchange.properties: ${exchange.getAllProperties()}")
          .process(
              exchange -> {
                var cause = getException(exchange);
                log.error("Exception during route " + exchange.getFromRouteId(), cause);
              });
    }

    if (exceptionHandler != null) {
      onExceptionDef.process(
          exchange -> {
            T cause = getException(exchange);
            try {
              O result = exceptionHandler.apply(exchange, cause);
              // Set the output message
              exchange.getMessage().setBody(result);
            } catch (Exception e) {
              log.error("Trying to handleFunction.apply()", e);
              throw e;
            }
          });
    }

    return onExceptionDef;
  }

  @SuppressWarnings("unchecked")
  public static <T extends Throwable> T getException(Exchange exchange) {
    // Since exchange.getException() returns null, get it from the properties, as shown at
    // https://camel.apache.org/manual/exception-clause.html#_using_a_processor_as_a_failure_handler
    // https://camel.apache.org/manual/faq/why-is-the-exception-null-when-i-use-onexception.html
    return (T) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
  }

  public static String getExceptionMessage(Exchange exchange) {
    return getException(exchange).getMessage();
  }

  /**
   * In case {@link #getExceptionMessage} doesn't return the desired exception message. This is
   * slower since it uses Camel's simple language to evaluate "${exception.message}".
   */
  public static String getOtherExceptionMessage(Exchange exchange, RouteBuilder builder) {
    return builder.exceptionMessage().evaluate(exchange, String.class);
  }
}
