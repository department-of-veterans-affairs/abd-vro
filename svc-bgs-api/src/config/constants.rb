# Contains constant definitions for queues, exchanges, and fields

BUNNY_ARGS = {
  host: ENV['RABBITMQ_PLACEHOLDERS_HOST'] || "localhost",
  user: ENV['RABBITMQ_USERNAME'] || "guest",
  password: ENV['RABBITMQ_PASSWORD'] || "guest"
}

REQUESTS_EXCHANGE = "svc_bgs_api.requests"
EXCHANGE_PROPERTIES = { durable: true, auto_delete: false }

ADD_NOTE_QUEUE = "svc_bgs_api.add_note"
ADD_NOTE_QUEUE_PROPERTIES = { durable: true, auto_delete: true }


HEALTHCHECK_EXCHANGE = "svc_bgs_api.healthcheck"
HEALTHCHECK_EXCHANGE_PROPERTIES = { durable: true, auto_delete: true }
HEALTHCHECK_QUEUE = "svc_bgs_api.healthcheck"
HEALTHCHECK_QUEUE_PROPERTIES = { durable: true, auto_delete: true}
