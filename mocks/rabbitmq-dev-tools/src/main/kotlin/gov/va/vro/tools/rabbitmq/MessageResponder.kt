package gov.va.vro.tools.rabbitmq

import org.springframework.amqp.AmqpException
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.InvocationResult
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter

private val log = mu.KotlinLogging.logger {}

class MessageResponder(
  private val rabbitTemplate: RabbitTemplate,
  queueName: String,
  private val responseDelayMillis: Long = System.getenv("RESPONSE_DELAY_MILLIS")?.toLong() ?: 500L,
  private val responseStatusCode: Int = 200,
  private val responseStatusMessage: String = "MessageResponder's response",
  val callback: (request: Message, responseBody: Any?) -> Unit = { _, _ -> }
) {
  private val helper = MessageHelper(rabbitTemplate)

  private val container =
    SimpleMessageListenerContainer(rabbitTemplate.connectionFactory).also {
      // Listen for messages in queue
      it.setQueueNames(queueName)
      it.setMessageListener(MyListenerAdaptor(this))
    }

  fun start() = container.start()

  fun stop() = container.shutdown()

  // Spring expects method `handleMessage` that takes args from `buildListenerArguments(...)`
  @Suppress("UNUSED_PARAMETER")
  fun handleMessage(message: Message, extractedMessage: Any): Any? {
    try {
      log.info("Got request")
      val body = helper.interpretBody(message)

      Thread.sleep(responseDelayMillis)

      log.info("Responding")
      // Modify the body to use as the response
      when (body) {
        is MutableMap<*, *> -> {
          when (val header = body["header"]) {
            is MutableMap<*, *> -> {
              log.info("  Set response header")
              @Suppress("UNCHECKED_CAST")
              (header as MutableMap<String, Any>).let {
                it["statusCode"] = responseStatusCode
                it["statusMessage"] = responseStatusMessage
              }
            }
          }
        }
      }

      callback(message, body)
      return helper.buildMessage(body)
    } catch (e: Exception) {
      e.printStackTrace()
      log.warn("Not responding!")
      return null
    }
  }
}

private class MyListenerAdaptor(listener: MessageResponder) : MessageListenerAdapter(listener) {
  // Override to pass original Message to the `handleMessage` method
  override fun buildListenerArguments(
    extractedMessage: Any,
    channel: Channel,
    message: Message
  ): Array<out Any> {
    return arrayOf<Any>(message, extractedMessage)
  }

  // Override to handle missing ReplyTo
  override fun handleResult(resultArg: InvocationResult?, request: Message?, channel: Channel?) {
    try {
      super.handleResult(resultArg, request, channel)
    } catch (e: AmqpException) {
      if (e.message!!.contains("Cannot determine ReplyTo message property value"))
        log.warn("No ReplyTo message property; not sending reply")
      else e.printStackTrace()
    }
  }
}
