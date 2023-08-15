package gov.va.vro.tools.rabbitmq

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate

private val log = mu.KotlinLogging.logger {}

class MessageHelper(
  private val rabbitTemplate: RabbitTemplate,
) {

  fun buildMessage(msg: Any): Message {
    log.info("  buildMessage(${msg.javaClass.simpleName}): $msg")
    return when {
      msg is Message -> msg
      // For a JSON String, don't use rabbitTemplate.messageConverter
      // Convert to ByteArray manually b/c Jackson2JsonMessageConverter converts to a JSON String
      msg is String && isJSON(msg) -> Message(msg.toByteArray())
      else -> {
        rabbitTemplate.messageConverter.toMessage(msg, MessageProperties()).also {
          // Not necessary but helps to keep the MessageProperties consistent
          // with the JSON String Message above
          stripExtraMessageProperties(it)
        }
      }
    }.also {
      log.info { "  bodyString: ${String(it.body)}" }
      log.info { "  ${it.messageProperties}" }
    }
  }

  private val mapper: ObjectMapper =
    ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)

  private fun isJSON(str: String): Boolean {
    try {
      mapper.readTree(str)
      return true
    } catch (e: JacksonException) {
      return false
    }
  }

  private fun stripExtraMessageProperties(message: Message) {
    log.info { "Stripping extra message properties set by Spring" }
    message.messageProperties.let {
      it.headers.remove("__TypeId__")
      it.contentType = null
      it.contentEncoding = null
    }
  }

  fun interpretBody(msg: Message): Any {
    try {
      log.info { "  bodyString: ${String(msg.body)}" }
      log.info { "  ${msg.messageProperties}" }
      return rabbitTemplate.messageConverter.fromMessage(msg).also {
        log.info { "  bodyConverted (${it.javaClass}): $it" }
      }
    } catch (e: Exception) {
      log.warn { "  Could not convert body! Returning body as String" }
      return String(msg.body)
    }
  }
}
