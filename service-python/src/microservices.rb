#!/usr/bin/env ruby
require_relative 'config/setup'
require 'rabbit_subscriber'
require 'logger'
require 'health_data_assessor'
require 'fast_track_pdf_generator'

$logger = Logger.new(STDOUT)
$logger.level = Logger::DEBUG

bunny_args = {
  host: ENV['RABBITMQ_PLACEHOLDERS_HOST'] || "localhost",
  user: ENV['RABBITMQ_PLACEHOLDERS_USERNAME'] || "guest",
  password: ENV['RABBITMQ_PLACEHOLDERS_USERPASSWORD'] || "guest"
}
subscriber = RabbitSubscriber.new(bunny_args)

def setup_queues(subscriber)
  subscriber.setup_queue(exchange_name: 'assess_health_data', queue_name: 'health_data_assessor')
  subscriber.setup_queue(exchange_name: 'generate_pdf', queue_name: 'pdf_generator')
end
setup_queues(subscriber)

def subscribe_assessor(subscriber)
  subscriber.subscribe_to('assess_health_data', 'health_data_assessor') do |json|
    HealthDataAssessor.new.assess(json['contention'], json)
  end
end
def subscribe_pdf_generator(subscriber)
  subscriber.subscribe_to('generate_pdf', 'pdf_generator') do |json|
    disability_type = json['contention'].to_sym
    compiled_pdf = FastTrackPdfGenerator.new(json['patient_info'], json['assessed_data'], disability_type).generate
    filename = "rrd-pdf-#{Time.now.to_i}.pdf"
    compiled_pdf.render_file(filename)
    {
      filename: filename
    }
  end
end
subscribe_assessor(subscriber)
subscribe_pdf_generator(subscriber)

def listen_for_messages(subscriber)
  while true do
    $logger.info "Waiting for messages..."
    sleep 10.minutes
  end
ensure
  $logger.info "Closing queue subscriptions"
  subscriber.close
end

listen_for_messages(subscriber)
