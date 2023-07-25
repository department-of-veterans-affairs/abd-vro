package gov.va.vro.services.bie.service.kafka;

import gov.va.vro.services.bie.config.BieProperties;
import gov.va.vro.services.bie.service.AmqpMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class KafkaConsumer {
  @Autowired AmqpMessageSender amqpMessageSender;
  @Autowired BieProperties bieProperties;
  private final Schema schema;

  public KafkaConsumer() throws IOException {
    // Load the Avro schema
    try (InputStream schemaStream =
        getClass().getResourceAsStream("/avro/ContentionAssociatedToClaim.avsc")) {
      this.schema = new Schema.Parser().parse(schemaStream);
    }
  }

  @KafkaListener(
      topics = {"#{'${kafka.topic.prefix}'}_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02"})
  public void consume(
      ConsumerRecord<byte[], byte[]> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
      throws IOException {
    try {
      byte[] value = record.value();
      log.info("Topic name: " + topic);
      log.info("Consumed message key: " + new String(record.key(), StandardCharsets.UTF_8));
      log.info(
          "Consumed message value (before) decode: " + new String(value, StandardCharsets.UTF_8));

      // Create a reader for the Avro schema
      DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
      // Create a decoder for the Avro-encoded data
      Decoder decoder = DecoderFactory.get().binaryDecoder(value, null);
      // Deserialize the data
      GenericRecord genericRecord = reader.read(null, decoder);

      log.info("Consumed message value (after) : " + genericRecord);
      log.info("Consumed message value(toString): " + genericRecord.toString());
      amqpMessageSender.send(
          bieProperties.getKafkaTopicToAmqpQueueMap().get(topic), topic, genericRecord.toString());
    } catch (Exception e) {
      handleException(e);
    }
  }

  @ExceptionHandler
  public void handleException(Exception e) {
    // Log the exception and handle it
    log.error("Exception occurred while processing message: " + e.getMessage());
  }
}
