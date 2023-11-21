require 'logger'
require 'active_support'

require_relative 'config/setup'

require 'rabbit_subscriber'
require 'bgs_client'

BUNNY_ARGS = {
  host: ENV['RABBITMQ_PLACEHOLDERS_HOST'].presence || "localhost",
  user: ENV['RABBITMQ_USERNAME'].presence || "guest",
  password: ENV['RABBITMQ_PASSWORD'].presence || "guest"
}

def initialize_subscriber(bgs_client)
  subscriber = RabbitSubscriber.new(BUNNY_ARGS)
  subscriber.setup_queue(exchange_name: 'bgs-api', queue_name: 'add-note')
  subscriber.subscribe_to('bgs-api', 'add-note') do |json|
    $logger.info "Subscriber received request #{json}"
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

$logger.info "Initializing subscriber..."
subscriber = initialize_subscriber(BgsClient.new)
$logger.info "Initialized subscriber!"
run(subscriber)
