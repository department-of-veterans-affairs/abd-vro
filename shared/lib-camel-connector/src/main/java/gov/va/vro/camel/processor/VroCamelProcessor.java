package gov.va.vro.camel.processor;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.camel.Exchange;

import java.util.Optional;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
abstract class VroCamelProcessor {

  @SuppressWarnings("unchecked")
  <I> I getInputBody(Exchange exchange, Class<I> inputBodyClass) {
    if (inputBodyClass == null) return (I) exchange.getIn().getBody();

    return exchange.getIn().getBody(inputBodyClass);
  }

  void conditionallySetOutputBody(Exchange exchange, Object body) {
    switch (exchange.getPattern()) {
      case InOut -> exchange.getMessage().setBody(body);
      case InOptionalOut -> {
        if (body == null || (body instanceof Optional && ((Optional) body).isEmpty())) break;
        exchange.getMessage().setBody(body);
      }
      default -> {}
    }
  }
}
