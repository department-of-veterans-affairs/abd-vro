package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.model.biekafka.BieMessagePayload;
import gov.va.vro.services.bie.config.BieProperties;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class KafkaConsumerCreator {

  private final List<KafkaMessageListenerContainer<?, ?>> listeners = new ArrayList<>();

  public KafkaConsumerCreator(
      final ConsumerFactory<?, ?> consumerFactory,
      final AmqpMessageSender amqpMessageSender,
      final BieProperties bieProperties) {
    setUpListeners(consumerFactory, amqpMessageSender, bieProperties.getKafkaTopicToAmqpQueueMap());
  }

  private void setUpListeners(
      final ConsumerFactory<?, ?> consumerFactory,
      final AmqpMessageSender amqpMessageSender,
      final Map<String, String> topicMap) {
    topicMap.forEach(
        (kafkaTopic, amqpExchange) -> {
          final ContainerProperties containerProps = new ContainerProperties(kafkaTopic);
          containerProps.setAckMode(ContainerProperties.AckMode.RECORD);
          containerProps.setMessageListener(
              (MessageListener<Integer, String>)
                  data -> {
                    log.debug(
                        "event=messageReceivedFromKafka topic={} msg={}",
                        data.topic(),
                        data.value());
                    final BieMessagePayload payload = BieMessagePayload.builder().build();
                    payload.setClaimId(data.value());
                    payload.setEvent(data.topic());
                    payload.setNotifiedAt(String.valueOf(data.value()));
                    amqpMessageSender.send(amqpExchange, data.topic(), payload);
                  });
          listeners.add(new KafkaMessageListenerContainer<>(consumerFactory, containerProps));
        });
  }

  public List<KafkaMessageListenerContainer<?, ?>> getListeners() {
    return List.copyOf(listeners);
  }

  @PostConstruct
  public void startUp() {
    listeners.forEach(AbstractMessageListenerContainer::start);
  }

  @PreDestroy
  public void tearDown() {
    listeners.forEach(AbstractMessageListenerContainer::stop);
  }
}
