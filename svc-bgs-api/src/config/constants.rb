# Contains constant definitions for queues, exchanges, and fields

BUNNY_ARGS = {
  host: ENV['RABBITMQ_PLACEHOLDERS_HOST'] || "localhost",
  user: ENV['RABBITMQ_USERNAME'] || "guest",
  password: ENV['RABBITMQ_PASSWORD'] || "guest"
}

CAMEL_MQ_PROPERTIES = { durable: true, auto_delete: true }

BGS_EXCHANGE_NAME = "bgs-api"

HEALTHCHECK_REPLY_QUEUE = "healthcheck-reply"
HEALTHCHECK_QUEUE = "healthcheck"
ADD_NOTE_QUEUE = "add-note"
