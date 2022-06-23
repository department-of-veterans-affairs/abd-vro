#!/usr/bin/env ruby
# Inspired from https://www.cloudbees.com/blog/writing-microservice-in-ruby
require 'bunny'
require 'json'
require 'pry'

CAMEL_MQ_PROPERTIES = { durable: true, auto_delete: true }
EXCHANGE_NAME = 'assess_health_data'
SERVICE_QUEUE_NAME = 'health_data_assessor'
REPLY_QUEUE_NAME = 'example_assess'

# http://rubybunny.info/articles/queues.html
def setup_queue(channel, exchange_name:, queue_name:)
  queue = channel.queue(queue_name, CAMEL_MQ_PROPERTIES)
  # https://www.baeldung.com/java-rabbitmq-exchanges-queues-bindings
  queue.bind(exchange_name, routing_key: queue_name)

  # Display counts
  puts queue.message_count
  puts queue.consumer_count
end

connection = Bunny.new
connection.start
channel = connection.create_channel
setup_queue(channel, exchange_name: EXCHANGE_NAME, queue_name: SERVICE_QUEUE_NAME)

def reply_listener(channel, exchange_name:, queue_name:)
  queue = channel.queue(queue_name, CAMEL_MQ_PROPERTIES)
  queue.bind(exchange_name, routing_key: queue_name)

  queue.subscribe(block: false) do |delivery_info, properties, body|
    puts "Received reply: #{JSON.pretty_generate(JSON.parse(body))}"
    puts " correlation_id: #{properties.correlation_id}"
  end
  queue
end
reply_queue = reply_listener(channel, exchange_name: EXCHANGE_NAME, queue_name: REPLY_QUEUE_NAME)

payload = {
  contention: 'hypertension',
  bp_observations: JSON.parse(File.read("examples/lighthouse_observations_resp.json"))
}

publish_direct_to_queue=false
if (publish_direct_to_queue)
  queue = channel.queue(SERVICE_QUEUE_NAME, CAMEL_MQ_PROPERTIES)
  queue.publish(payload.to_json,
                reply_to: REPLY_QUEUE_NAME,
                correlation_id: Time.now.to_s
               )
else
  exchange = channel.direct(EXCHANGE_NAME, CAMEL_MQ_PROPERTIES)
  exchange.publish(payload.to_json,
                   routing_key: SERVICE_QUEUE_NAME,
                   reply_to: REPLY_QUEUE_NAME,
                   correlation_id: Time.now.to_s
                  )
end
puts "Published payload"

puts "Waiting for reply within 5 seconds"
sleep 5
puts "Closing"
connection.close
