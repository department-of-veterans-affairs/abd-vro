package gov.va.vro.services.bie.service;

import gov.va.vro.model.biekafka.BieMessagePayload;

public interface AmqpMessageSender {

  void send(String exchange, String routingKey, BieMessagePayload message);
}
