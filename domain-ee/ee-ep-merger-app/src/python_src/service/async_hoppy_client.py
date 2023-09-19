import json
import logging
import time
import uuid

from async_consumer import AsyncConsumer
from async_publisher import AsyncPublisher
from pika import BasicProperties, ConnectionParameters
from response_exception import ResponseException


class AsyncHoppyClient:
    MAX_RETRIES = 3
    MAX_LATENCY = 30
    RESPONSE_REJECT_AND_REQUEUE_ATTEMPTS = 3

    responses = {}
    rejected = {}

    def __init__(self,
                 name: str,
                 connection_parameters: ConnectionParameters,
                 exchange: str,
                 request_queue: str,
                 reply_to_queue: str):
        self.exchange = exchange
        self.request_queue = request_queue
        self.reply_to_queue = reply_to_queue

        self.async_publisher = AsyncPublisher(exchange=exchange,
                                              exchange_type="direct",
                                              queue=request_queue,
                                              routing_key=request_queue,
                                              connection_parameters=connection_parameters)

        self.async_consumer = AsyncConsumer(exchange=exchange,
                                            exchange_type="direct",
                                            queue=reply_to_queue,
                                            routing_key=reply_to_queue,
                                            connection_parameters=connection_parameters,
                                            reply_callback=self.on_reply)

    def start(self, loop):
        self.async_publisher.connect(loop)
        self.async_consumer.connect(loop)

    def stop(self):
        self.async_publisher.stop()
        self.async_consumer.stop()

    def make_request(self, request_id, body):
        attempt = 0
        while attempt < self.MAX_RETRIES:
            try:
                response = self.make_single_request(request_id, body)
                if response:
                    return response
            except ResponseException:
                attempt += 1
                continue
        logging.warning(f"event=requestError "
                        f"id={request_id} "
                        f"queue={self.request_queue} "
                        f"error='Max retries reached'")
        raise ResponseException(message="Max retries reached")

    def make_single_request(self, request_id, body):
        correlation_id = str(uuid.uuid4())
        logging.info(
            f"event=requestStarted id={request_id} queue={self.request_queue} correlation_id={correlation_id}")
        self.responses[correlation_id] = None

        self.async_publisher.publish_message(body,
                                             BasicProperties(app_id="ep_merger",
                                                             content_type="application/json",
                                                             reply_to=self.reply_to_queue,
                                                             correlation_id=correlation_id))

        retry_time = None
        while True:
            response = self.responses.get(correlation_id)
            if response:
                self.terminate_correlation_id(correlation_id)
                logging.info(
                    f"event=requestCompleted id={request_id} queue={self.request_queue} correlation_id={correlation_id}")
                return response
            else:
                if retry_time is None:
                    retry_time = time.time()
                elif time.time() - retry_time >= self.MAX_LATENCY:
                    logging.warning(f"event=requestError "
                                    f"id={request_id} "
                                    f"queue={self.request_queue} "
                                    f"correlation_id={correlation_id} "
                                    f"error='Request timed out'")
                    self.terminate_correlation_id(correlation_id)
                    raise ResponseException(message="Request timed out")

    def terminate_correlation_id(self, correlation_id):
        if correlation_id in self.responses.keys():
            del self.responses[correlation_id]
        if correlation_id in self.rejected.keys():
            del self.rejected[correlation_id]

    def on_reply(self, channel, properties, delivery_tag, body):
        cor_id = properties.correlation_id

        if cor_id and cor_id in self.responses.keys():
            self.log_response_event("responseAcked", cor_id, True, False, 1)
            response = json.loads(body)
            self.responses[cor_id] = response
            self.async_consumer.acknowledge_message(delivery_tag)
            return

        if not cor_id:
            self.log_response_event("responseRejected", cor_id, False, False, 1)
            self.async_consumer.reject_message(delivery_tag, requeue=False)
            return

        num_rejections = self.rejected.get(cor_id, 0) + 1
        if num_rejections < self.RESPONSE_REJECT_AND_REQUEUE_ATTEMPTS:
            self.log_response_event("responseRejected", cor_id, False, True, num_rejections)
            self.rejected[cor_id] = num_rejections
            self.async_consumer.reject_message(delivery_tag, requeue=True)
        else:
            self.log_response_event("responseRejected", cor_id, False, False, num_rejections)
            self.async_consumer.reject_message(delivery_tag, requeue=False)
            if cor_id in self.rejected:
                del self.rejected[cor_id]

    def log_response_event(self, action: str, correlation_id: str, correlated: bool, requeued: bool, times_processed):
        logging.info(
            f"event={action} "
            f"queue={self.request_queue} "
            f"correlation_id={correlation_id} "
            f"correlated={correlated} "
            f"requeued={requeued} "
            f"times_processed={times_processed}")
