# This script is used by the K8S Readiness probe to check the availability of the BGS-api service as well as the status of RabbitMQ thereby verifying 
# that the BGS-api application is ready to perform its basic functions. 

require_relative '../lib/rabbit_subscriber'
require_relative 'lib/bgs_client'

def rabbitmq_connection_active?
  subscriber = RabbitSubscriber.new(BUNNY_ARGS)
  subscriber.connected?
rescue StandardError => e
  puts "RabbitMQ connection check failed: #{e.message}"
  false
end

def bgs_service_available?
  client = BgsClient.new
  client.check_bgs_availability
rescue StandardError => e
  puts "BGS service availability check failed: #{e.message}"
  false
end

if rabbitmq_connection_active? && bgs_service_available?
  exit 0
else
  puts "Readiness check failed"
  exit 1
end

