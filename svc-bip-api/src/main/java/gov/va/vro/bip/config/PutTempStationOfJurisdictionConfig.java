package gov.va.vro.bip.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PutTempStationOfJurisdictionConfig {

  final DirectExchange bipApiExchange;
  final String putTempStationOfJurisdictionQueue;

  public PutTempStationOfJurisdictionConfig(
      @Value("${putTempStationOfJurisdictionQueue}") final String putTempStationOfJurisdictionQueue,
      final DirectExchange bipApiExchange) {
    this.putTempStationOfJurisdictionQueue = putTempStationOfJurisdictionQueue;
    this.bipApiExchange = bipApiExchange;
  }

  @Bean
  Queue putTempStationOfJurisdictionQueue() {
    return new Queue(putTempStationOfJurisdictionQueue, true, false, true);
  }

  @Bean
  Binding putTempStationOfJurisdictionBinding() {
    return BindingBuilder.bind(putTempStationOfJurisdictionQueue())
        .to(bipApiExchange)
        .with(putTempStationOfJurisdictionQueue);
  }
}
