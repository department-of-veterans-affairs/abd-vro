package gov.va.vro.services.bie.service.amqp;

public interface AmqpMessageSender {

  void send(String exchange, String routingKey, Object message);
}
