# Inspired from https://www.cloudbees.com/blog/writing-microservice-in-ruby
require 'bunny'
require 'json'
require 'health_data_assessor'
require 'fast_track_pdf_generator'
require 'pry'

class RabbitSubscriber

  def initialize(bunny_args)
    @connection = Bunny.new(bunny_args)
    connect(@connection)
    @exchanges = {}
    @queues = {}

    setup_queue(exchange_name: 'assess_health_data', queue_name: 'health_data_assessor')
    setup_queue(exchange_name: 'generate_pdf', queue_name: 'pdf_generator')
  end

  def connect(connection)
    attempts ||= 1
    connection.start
  rescue => error
    puts error.message
    if (attempts += 1) < 5
      puts "Retrying to connect to RabbitMQ in 10 seconds"
      sleep 10
      retry
    else
      raise error
    end
  end

  def close
    @connection.close
  end

  CAMEL_MQ_PROPERTIES = { durable: true, auto_delete: true }

  def setup_queue(exchange_name:, queue_name:)
    channel = @connection.create_channel
    # Exchange and queue properties must match those set up in Camel
    # http://rubybunny.info/articles/exchanges.html
    exchange = channel.direct(exchange_name, CAMEL_MQ_PROPERTIES)
    @exchanges[exchange_name] = exchange
    # in case message cannot be delivered
    exchange.on_return do |return_info, properties, content|
      puts "Got a returned message: #{content}"
    end

    channel.queue(queue_name, CAMEL_MQ_PROPERTIES).tap do |queue|
      @queues[queue_name] = queue
      # https://www.baeldung.com/java-rabbitmq-exchanges-queues-bindings
      queue.bind(exchange_name, routing_key: queue_name)
    end
  end

  def subscribe
    subscribe_assessor
    subscribe_pdf_generator
  end
  def subscribe_assessor
    subscribe_to('assess_health_data', 'health_data_assessor') do |json|
      HealthDataAssessor.new.assess(json['contention'], json)
    end
  end
  def subscribe_pdf_generator
    subscribe_to('generate_pdf', 'pdf_generator') do |json|
      disability_type = json['contention'].to_sym
      compiled_pdf = FastTrackPdfGenerator.new(json['patient_info'], json['assessed_data'], disability_type).generate
      filename = "rrd-pdf-#{Time.now.to_i}.pdf"
      compiled_pdf.render_file(filename)
      {
        filename: filename
      }
    end
  end
  def subscribe_to(exchange_name, queue_name)
    begin
      queue = @queues[queue_name]
      puts " [*] Waiting for messages for queue #{queue_name}. To exit press CTRL+C"
      queue.subscribe do |delivery_info, properties, body|
        puts " [x] #{queue_name}: Received body with size: #{body.size}"
        puts "reply_to: #{properties.reply_to}"
        puts "correlation_id: #{properties.correlation_id}"
        puts "Headers: #{properties.headers}"
        puts "delivery_info: #{delivery_info}"
        # delivery_info.routing_key is the same as queue_name in this case

        json = JSON.parse(body)
        response = yield(json)
      rescue => e
        puts e.backtrace
        response = {
          error_message: e.message,
          backtrace: e.backtrace.join("\n ")
        }
      ensure
        puts "Response: #{response}"
        # delivery_info.exchange or exchange_name can be used
        exchange = @exchanges[exchange_name]
        exchange.publish(
          response.to_json,
          routing_key: properties.reply_to,
          correlation_id: properties.correlation_id
        )
      end
    rescue Interrupt => _
      @connection.close
      exit(0)
    end
  end
end
