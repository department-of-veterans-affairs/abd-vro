# This script is initializes a BgsClient instance and performs a specific readiness check using the svc-bgs-api application logic. 
# This script provides more specific error feedback by catching and printing exceptions related to the actual operation of your application.

require_relative 'rabbit_subscriber'
require_relative 'lib/bgs_client'

def rabbitmq_connection_active?
  subscriber = RabbitSubscriber.new(BUNNY_ARGS)
  subscriber.connected?
rescue
  false
end

def bgs_service_available?
  client = BgsClient.new
  client.check_bgs_availability
rescue
  false
end

if rabbitmq_connection_active? && bgs_service_available?
  exit 0
else
  puts "Readiness check failed"
  exit 1
end

