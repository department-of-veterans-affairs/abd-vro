import amqp from 'amqplib';
import { v4 as uuidv4 } from 'uuid';

const queueName = 'serviceJ';
const requestMessage = JSON.stringify({
    "resourceId": "1234",
    "diagnosticCode":"J"
});
const replyQueueName = 'replyQueue';
const amqpUrl = 'amqp://localhost';

(async () => {
  try {
    // Establish a connection to the RabbitMQ server
    const connection = await amqp.connect(amqpUrl);
    const channel = await connection.createChannel();

    // Assert the reply queue
    await channel.assertQueue(replyQueueName, { exclusive: true });

    // Set up a consumer to listen for the response
    await channel.consume(replyQueueName, (msg) => {
      console.log('Received:', msg.content.toString());

      // Close the channel and connection after receiving the response
      channel.close();
      connection.close();
    }, { noAck: true });

    // Publish a message to the queue with reply-to and correlationId properties
    const correlationId = uuidv4();
    channel.publish('', queueName, Buffer.from(requestMessage), {
      replyTo: replyQueueName,
      correlationId,
    });
    console.log('Sent:', requestMessage);

  } catch (error) {
    console.error(error);
  }
})();