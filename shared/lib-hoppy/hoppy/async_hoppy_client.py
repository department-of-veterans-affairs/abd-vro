import asyncio
import json
import logging
import time
import uuid

from hoppy.async_consumer import AsyncConsumer
from hoppy.async_publisher import AsyncPublisher
from hoppy.exception import ResponseException
from pika import BasicProperties, ConnectionParameters


class AsyncHoppyClient:
    responses = {}
    rejected = {}

    def __init__(self,
                 name: str,
                 app_id: str,
                 connection_parameters: ConnectionParameters,
                 exchange: str,
                 request_queue: str,
                 reply_to_queue: str,
                 max_latency: int = 30,
                 response_reject_and_requeue_attempts: int = 3):
        self.name = name
        self.app_id = app_id
        self.exchange = exchange
        self.request_queue = request_queue
        self.reply_to_queue = reply_to_queue
        self.max_latency = max_latency
        self.response_reject_and_requeue_attempts = response_reject_and_requeue_attempts

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
                                            reply_callback=self._on_reply)

    def start(self, loop):
        self.async_publisher.connect(loop)
        self.async_consumer.connect(loop)

    def stop(self):
        self.async_publisher.stop()
        self.async_consumer.stop()

    async def make_request(self, request_id, body):
        correlation_id = str(uuid.uuid4())
        logging.info(
            f"event=requestStarted id={request_id} "
            f"client={self.name} "
            f"queue={self.request_queue} "
            f"correlation_id={correlation_id}")
        self.responses[correlation_id] = None

        try:
            self.async_publisher.publish_message(body,
                                                 BasicProperties(app_id=self.app_id,
                                                                 content_type="application/json",
                                                                 reply_to=self.reply_to_queue,
                                                                 correlation_id=correlation_id))
        except Exception as e:
            logging.warning(f"event=requestError "
                            f"client={self.name} "
                            f"id={request_id} "
                            f"queue={self.request_queue} "
                            f"correlation_id={correlation_id} "
                            f"error='Unexpected Error While Publishing {e}'")
            raise ResponseException(message="Unexpected Error Caught")

        wait_for_response_time = None
        while True:
            response = self.responses.get(correlation_id)
            if response:
                self._terminate_correlation_id(correlation_id)
                logging.info(f"event=requestCompleted "
                             f"client={self.name} "
                             f"id={request_id} "
                             f"queue={self.request_queue} "
                             f"correlation_id={correlation_id}")
                return response
            else:
                if wait_for_response_time is None:
                    wait_for_response_time = time.time()
                    await asyncio.sleep(0)
                elif time.time() - wait_for_response_time >= self.max_latency:
                    logging.warning(f"event=requestError "
                                    f"client={self.name} "
                                    f"id={request_id} "
                                    f"queue={self.request_queue} "
                                    f"correlation_id={correlation_id} "
                                    f"error='Request timed out'")
                    self._terminate_correlation_id(correlation_id)
                    raise ResponseException(message="Request timed out")

    def _terminate_correlation_id(self, correlation_id):
        if correlation_id in self.responses.keys():
            del self.responses[correlation_id]
        if correlation_id in self.rejected.keys():
            del self.rejected[correlation_id]

    def _on_reply(self, channel, properties, delivery_tag, body):
        cor_id = properties.correlation_id

        if cor_id and cor_id in self.responses.keys():
            self._log_response_event("responseAcked", cor_id, True, False, 1)
            response = json.loads(body)
            self.responses[cor_id] = response
            self.async_consumer.acknowledge_message(delivery_tag)
            return

        if not cor_id:
            self._log_response_event("responseRejected", cor_id, False, False, 1)
            self.async_consumer.reject_message(delivery_tag, requeue=False)
            return

        num_rejections = self.rejected.get(cor_id, 0) + 1
        if num_rejections < self.response_reject_and_requeue_attempts:
            self._log_response_event("responseRejected", cor_id, False, True, num_rejections)
            self.rejected[cor_id] = num_rejections
            self.async_consumer.reject_message(delivery_tag, requeue=True)
        else:
            self._log_response_event("responseRejected", cor_id, False, False, num_rejections)
            self.async_consumer.reject_message(delivery_tag, requeue=False)
            if cor_id in self.rejected:
                del self.rejected[cor_id]

    def _log_response_event(self, action: str, correlation_id: str, correlated: bool, requeued: bool, times_processed):
        logging.info(
            f"event={action} "
            f"client={self.name} "
            f"queue={self.request_queue} "
            f"correlation_id={correlation_id} "
            f"correlated={correlated} "
            f"requeued={requeued} "
            f"times_processed={times_processed}")


class RetryableAsyncHoppyClient(AsyncHoppyClient):
    def __init__(self,
                 name: str,
                 app_id: str,
                 connection_parameters: ConnectionParameters,
                 exchange: str,
                 request_queue: str,
                 reply_to_queue: str,
                 max_latency: int = 30,
                 response_reject_and_requeue_attempts: int = 3,
                 max_retries=3):
        self.max_retries = max_retries
        super().__init__(name, app_id, connection_parameters, exchange, request_queue, reply_to_queue, max_latency,
                         response_reject_and_requeue_attempts)

    async def make_request(self, request_id, body):
        attempt = 0
        while attempt < self.max_retries:
            try:
                response = await super().make_request(request_id, body)
                if response:
                    return response
            except ResponseException:
                attempt += 1
                continue
        logging.warning(f"event=requestError "
                        f"client={self.name} "
                        f"id={request_id} "
                        f"queue={self.request_queue} "
                        f"error='Max retries reached'")
        raise ResponseException(message="Max retries reached")
