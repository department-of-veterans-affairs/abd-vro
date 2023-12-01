# Inspired by the README at https://github.com/ruby-amqp/bunny

require 'bunny'
require 'json'

require_relative '../config/constants'

conn = Bunny.new(BUNNY_ARGS)
conn.start

ch = conn.create_channel
x = ch.direct(BGS_EXCHANGE_NAME, CAMEL_MQ_PROPERTIES)

r = ch.queue(HEALTHCHECK_REPLY_QUEUE, CAMEL_MQ_PROPERTIES).bind(x, :routing_key => HEALTHCHECK_REPLY_QUEUE)

q = ch.queue(HEALTHCHECK_QUEUE, CAMEL_MQ_PROPERTIES).bind(x, :routing_key => HEALTHCHECK_QUEUE)
q.publish('{"health": "check"}', :reply_to => HEALTHCHECK_REPLY_QUEUE)

delivery_info, properties, payload = r.pop

if JSON.parse(payload)["statusCode"] != 200 then
    raise "svc-bgs-api healthcheck failed"
end
