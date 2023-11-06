package gov.va.vro.services.bie.service;

public interface AmqpMessageSender {

  void send(String exchange, String routingKey, Object message);
}
