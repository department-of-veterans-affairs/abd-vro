require 'logger'
require 'active_support'

require_relative 'config/setup'
require_relative 'config/constants'

require 'rabbit_subscriber'
require 'bgs_client'

def initialize_subscriber(bgs_client)
  subscriber = RabbitSubscriber.new(BUNNY_ARGS)

  # Setup queue/subscription for healthcheck
  subscriber.setup_queue(exchange_name: BGS_EXCHANGE_NAME, queue_name: HEALTHCHECK_QUEUE)
  subscriber.subscribe_to(BGS_EXCHANGE_NAME, HEALTHCHECK_QUEUE) do |json|
   $logger.info "Subscriber received healthcheck request #{json}"
   { statusCode: 200 }
  end
  subscriber

  # Setup queue/subscription for BGS
  subscriber.setup_queue(exchange_name: BGS_EXCHANGE_NAME, queue_name: ADD_NOTE_QUEUE)
  subscriber.subscribe_to(BGS_EXCHANGE_NAME, ADD_NOTE_QUEUE) do |json|
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
