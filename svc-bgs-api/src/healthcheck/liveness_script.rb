# This script is used by the K8S Liveness probe to check the connection between RabbitMQ and BGS-api thereby verifying 
# that the BGS-api application is running and able to perform its basic functions. 

require_relative '../lib/rabbit_subscriber'

def check_rabbitmq_connection
  subscriber = RabbitSubscriber.new(BUNNY_ARGS)
  subscriber.connected?
rescue StandardError => e
  puts "RabbitMQ connection check failed: #{e.message}"
  false
end

if check_rabbitmq_connection
  exit 0
else
  puts "Liveness check failed"
  exit 1
end
