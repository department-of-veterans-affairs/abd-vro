package gov.va.vro.services.bie.service.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.va.vro.services.bie.config.BieProperties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    topics = {KafkaConsumerTest.TOPIC},
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@Disabled
class KafkaConsumerTest {

  public static final String TOPIC = "TST_CONTENTION_BIE_CONTENTION_ASSOCIATED_TO_CLAIM_V02";

  @Autowired private KafkaConsumer consumer;

  @Autowired private KafkaProducer<String, GenericRecord> producer;

  private final CountDownLatch latch = new CountDownLatch(1);
  private GenericRecord payloadReceived = null;

  private BieProperties bieProperties;
  private KafkaConsumer kafkaConsumer;

  @KafkaListener(topics = {TOPIC})
  public void listen(ConsumerRecord<String, GenericRecord> record) {
    payloadReceived = record.value();
    System.out.println("received payload='{}'" + payloadReceived);
    latch.countDown();
  }

  @Test
  public void givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived()
      throws Exception {
    Schema schema =
        new Schema.Parser()
            .parse(new File(("src/main/resources/avro/ContentionAssociatedToClaim.avsc")));
    GenericData.Record data = new GenericData.Record(schema);
    data.put("claimId", "1234567890");
    data.put("contentionId", "0987654321");
    data.put("contentionType", "BDD");
    data.put("contentionText", "This is a test");
    data.put("contentionCategory", "BDD");
    data.put("contentionCategoryCode", "BDD");
    data.put("contentionCategoryName", "BDD");
    data.put("contentionCategoryDescription", "BDD");
    data.put("contentionCategoryLongDescription", "BDD");
    data.put("contentionCategoryLongName", "BDD");
    data.put("contentionCategoryShortName", "BDD");
    data.put("contentionCategorySortOrder", "BDD");

    producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>(TOPIC, data));

    latch.await(5, TimeUnit.SECONDS);
    assertEquals(data, payloadReceived);
  }
}
