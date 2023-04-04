#!/usr/bin/env ruby

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

def initialize_subscriber(bgs_client)
  subscriber = RabbitSubscriber.new(bunny_args)
  subscriber.setup_queue(exchange_name: 'bgs-api', queue_name: 'add-note')
  subscriber.subscribe_to('bgs-api', 'add-note') do |json|
    puts "Subscriber received request #{json}"
    begin
      bgs_client.handle_request(json)
    rescue => e
      {
        statusCode: e.is_a?(ArgumentError) ? 400 : 500,
        statusMessage: "#{e.class}: #{e.message}"
      }
    else
      { statusCode: 200 }
    end
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

subscriber = initialize_subscriber(BgsClient.new)
run(subscriber)
