require 'logger'
require 'active_support'

require_relative 'config/setup'

require 'rabbit_subscriber'
require 'bgs_client'
require 'json'

BUNNY_ARGS = {
  host: ENV['RABBITMQ_PLACEHOLDERS_HOST'].presence || "localhost",
  user: ENV['RABBITMQ_PLACEHOLDERS_USERNAME'].presence || "guest",
  password: ENV['RABBITMQ_PLACEHOLDERS_USERPASSWORD'].presence || "guest"
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

def run(subscriber, bgs_client)
  begin
    while true do
      $logger.info "Waiting for messages..."
      # sleep 10.minutes
      make_request(bgs_client)
      sleep 1.minutes
    end
  ensure
    $logger.info "Closing queue subscriptions"
    subscriber.close
  end
end

def make_request(bgs_client)
  # Send claim notes request
  req = JSON.parse(File.read("claim_notes.txt"))
  puts "claim_notes_req=#{req}"
  begin
    response = bgs_client.handle_request(req)
  rescue => e
    puts e.backtrace
    response = {
      statusCode: e.is_a?(ArgumentError) ? 400 : 500,
      statusMessage: "#{e.class}: #{e.message}",
    }
  ensure
    puts "claim_notes_response=#{response}"
    stringify = JSON.generate(response)
    File.write("claim_notes_response.txt", stringify)
  end

  # Send veteran notes request
  req = JSON.parse(File.read("veteran_note.txt"))
  puts "veteran_note_req=#{req}"
  begin
    response = bgs_client.handle_request(req)
  rescue => e
    puts e.backtrace
    response = {
      statusCode: e.is_a?(ArgumentError) ? 400 : 500,
      statusMessage: "#{e.class}: #{e.message}",
    }
  ensure
    puts "veteran_note_response=#{response}"
    stringify = JSON.generate(response)
    File.write("veteran_note_response.txt", stringify)
  end
end

$logger.info "Initializing subscriber..."
bgs_client = BgsClient.new
subscriber = initialize_subscriber(bgs_client)
$logger.info "Initialized subscriber!"
run(subscriber, bgs_client)
