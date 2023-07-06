package gov.va.vro.tools.rabbitmq

import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate

class MessageConsumer(
  private val rabbitTemplate: RabbitTemplate,
  private val queueName: String,
  val callback: (request: Message, body: Any?) -> Unit = { _, _ -> }
) {

  private val helper = MessageHelper(rabbitTemplate)

  fun receive(): Any? {
    rabbitTemplate.receive(queueName)?.let { msg ->
      return helper.interpretBody(msg).also { callback(msg, it) }
    }

    // Timed out. To avoid: rabbitTemplate.setReceiveTimeout(-1)
    return null
  }
}
