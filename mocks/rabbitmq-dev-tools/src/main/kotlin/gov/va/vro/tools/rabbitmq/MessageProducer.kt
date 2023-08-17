package gov.va.vro.tools.rabbitmq

import org.springframework.amqp.rabbit.core.RabbitTemplate

class MessageProducer(
  private val rabbitTemplate: RabbitTemplate,
  private val exchangeName: String,
  private val routingKey: String
) {

  private val helper = MessageHelper(rabbitTemplate)

  fun send(msg: Any) {
    val message = helper.buildMessage(msg)
    rabbitTemplate.send(exchangeName, routingKey, message)
  }
}
