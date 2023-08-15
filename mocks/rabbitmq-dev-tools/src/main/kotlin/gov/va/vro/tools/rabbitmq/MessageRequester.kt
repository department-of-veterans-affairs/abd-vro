package gov.va.vro.tools.rabbitmq

import org.springframework.amqp.rabbit.core.RabbitTemplate

private val log = mu.KotlinLogging.logger {}

class MessageRequester(
  private val rabbitTemplate: RabbitTemplate,
  private val exchangeName: String,
  private val routingKey: String,
  private val responseTimeoutMillis: Long =
    System.getenv("RESPONSE_TIMEOUT_MILLIS")?.toLong() ?: 1000L
) {

  private val helper = MessageHelper(rabbitTemplate)

  fun request(msg: Any) {
    val message = helper.buildMessage(msg)

    rabbitTemplate.setReplyTimeout(responseTimeoutMillis)

    rabbitTemplate.sendAndReceive(exchangeName, routingKey, message)?.let { response ->
      log.info("Got response")
      helper.interpretBody(response)
    }
      ?: log.info { "No response" }

    // Destroy so it doesn't try to reconnect
    rabbitTemplate.destroy()
  }
}
