package gov.va.vro.services.bie.service;

public interface AmqpTopicSender {

    void send(String exchange, String topic, String message);

}
