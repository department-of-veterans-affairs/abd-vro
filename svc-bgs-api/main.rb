#!/usr/bin/env ruby

require 'active_support/time'
require 'logger'

require_relative 'config/setup'

require 'rabbit_subscriber'
require 'bgs_client'

$logger = Logger.new(STDOUT)
$logger.level = Logger::DEBUG
STDOUT.sync = true if ENVIRONMENT == "development"

bunny_args = {
  host: ENV['RABBITMQ_PLACEHOLDERS_HOST'] || "localhost",
  user: ENV['RABBITMQ_PLACEHOLDERS_USERNAME'] || "guest",
  password: ENV['RABBITMQ_PLACEHOLDERS_USERPASSWORD'] || "guest"
}

def initialize_subscriber
  subscriber = RabbitSubscriber.new(bunny_args)
  subscriber.setup_queue(exchange_name: 'svc-bgs-api', queue_name: 'development_notes')
  subscriber.subscribe_to('svc-bgs-api', 'development_notes') do |json|
    puts "Subscriber received request #{json}"
    {
      statusCode: 200
    }
  end
  subscriber
end

def run(subscriber)
  begin
    while true do
      $logger.info "Waiting for messages..."
      sleep 10.minutes
    end
  ensure
    $logger.info "Closing queue subscriptions"
    subscriber.close
  end
end

run(nil)
