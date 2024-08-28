# Inspired by the README at https://github.com/ruby-amqp/bunny

require 'bunny'
require 'json'

require_relative '../config/constants'

conn = Bunny.new(BUNNY_ARGS)
conn.start

channel = conn.create_channel

begin
  # Create healthcheck exchange

  exchange = channel.direct(HEALTHCHECK_EXCHANGE, HEALTHCHECK_EXCHANGE_PROPERTIES)

  # Create healthcheck reply_queue queue with auto generated name
  reply_queue = channel.queue('', { auto_delete: true, exclusive: true })
  reply_queue.bind(exchange, :routing_key => reply_queue.name)

  # Create healthcheck queue
  request_queue = channel.queue(HEALTHCHECK_QUEUE, HEALTHCHECK_QUEUE_PROPERTIES).bind(exchange, :routing_key => HEALTHCHECK_QUEUE)

  # Publish healthcheck message
  request_queue.publish('{"health": "check"}', :reply_to => reply_queue.name)

  # Check healthcheck reply_queue
  delivery_info, properties, payload = reply_queue.pop

  # Return if all three are nil because that means queue was empty
  return if delivery_info.nil? and properties.nil? and payload.nil?

  raise "svc-bgs-api healthcheck failed: no response payload=#{payload}" if payload.nil?

  json = JSON.parse(payload)
  raise "svc-bgs-api healthcheck failed: payload=#{payload}" if json["statusCode"] != 200

ensure
  channel.close
  conn.close
end
