# Inspired by the README at https://github.com/ruby-amqp/bunny

require 'bunny'

BUNNY_ARGS = {
  host: ENV['RABBITMQ_PLACEHOLDERS_HOST'].presence || "localhost",
  user: ENV['RABBITMQ_USERNAME'].presence || "guest",
  password: ENV['RABBITMQ_PASSWORD'].presence || "guest"
}

REPLY_EXCHANGE = "bgs-api"
REPLY_QUEUE = "healthcheck-reply"

conn = Bunny.new(BUNNY_ARGS)
conn.start

ch = conn.create_channel
ch.confirm_select

r = ch.queue(REPLY_EXCHANGE, REPLY_QUEUE)

q  = ch.queue("bgs-api", "healthcheck")
q.publish("{health: check}", :reply_to => REPLY_QUEUE)

delivery_info, properties, payload = r.pop

if payload.status_code != 200 {
    raise "svc-bgs-api healthcheck failed"
}
