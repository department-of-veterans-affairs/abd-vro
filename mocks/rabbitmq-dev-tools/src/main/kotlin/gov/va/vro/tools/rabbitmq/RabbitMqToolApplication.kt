package gov.va.vro.tools.rabbitmq

import java.util.concurrent.CountDownLatch
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val log = mu.KotlinLogging.logger {}

fun main(args: Array<String>) {
  runApplication<RabbitMqToolApplication>(*args)
}

@SpringBootApplication
class RabbitMqToolApplication(
  private val connectionFactory: ConnectionFactory,
  private val rabbitAdmin: AmqpAdmin,
  private val rabbitTemplate: RabbitTemplate
) : CommandLineRunner {

  override fun run(vararg args: String?) {
    log.info { connectionFactory }
    args.forEachIndexed { i, it -> log.info { "args[${i}]: $it" } }

    val exchangeName = System.getenv("MQ_EXCHANGE") ?: "mq-exchange"
    val queueName = System.getenv("MQ_QUEUE") ?: "mq-queue"
    val routingKey = System.getenv("MQ_ROUTING_KEY") ?: queueName

    when (args[0]) {
      "c" -> {
        val consumer = MessageConsumer(rabbitTemplate, queueName)
        repeat(getMsgCount(args.getOrNull(1)) ?: 1) {
          // Since queue auto-delete=true, when consumer is done, the queue is deleted,
          // so rebind each time
          bindQueue(exchangeName, queueName, routingKey)
          log.info("Consumer waiting for message ${it + 1}")
          consumer.receive() ?: log.info("Timed out")
        }
      }
      "p" -> {
        log.info("Producer")
        MessageProducer(rabbitTemplate, exchangeName, routingKey).send(getMsgBody())
      }
      "resp" -> {
        bindQueue(exchangeName, queueName, routingKey)
        val countDownLatch = CountDownLatch(getMsgCount(args.getOrNull(1)) ?: 100)
        log.info("Responder waiting for ${countDownLatch.count} message(s)")
        val countDown = { _: Message, _: Any? -> countDownLatch.countDown() }
        val responder = MessageResponder(rabbitTemplate, queueName, callback = countDown)
        responder.start()

        countDownLatch.await() // Wait until countDownLatch reaches 0
        log.info("(Last message received; shutting down in a few seconds...)")
        Thread.sleep(2000L) // Allow other threads to finish
        responder.stop()
      }
      "req" -> {
        log.info { "Requester" }
        val requester = MessageRequester(rabbitTemplate, exchangeName, routingKey)
        requester.request(getMsgBody())
      }
    }
    shutdown()
  }

  private fun getMsgBody(): Any {
    return System.getenv("MSG_BODY")
      ?:
      // Create a DTO object expected by svc-xample-j
      mapOf(
        "resourceId" to "54321",
        "diagnosticCode" to "J",
        "header" to mapOf("statusCode" to null)
      )
  }

  private fun getMsgCount(argString: String?) = argString?.let { it.toInt() }

  private fun shutdown() = (connectionFactory as CachingConnectionFactory).destroy()

  private fun bindQueue(exchangeName: String, queueName: String, routingKey: String) {
    rabbitTemplate.setReceiveTimeout(100_000L)

    rabbitAdmin.declareQueue(Queue(queueName, true, false, true))
    rabbitAdmin.declareExchange(DirectExchange(exchangeName, true, true))
    rabbitAdmin.declareBinding(
      Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, mapOf())
    )
  }
}

@Configuration
private class RabbitMqToolApplicationConfiguration {
  @Bean fun jsonMessageConverter(): MessageConverter = Jackson2JsonMessageConverter()
}
