# Inspired from https://www.cloudbees.com/blog/writing-microservice-in-ruby
require 'bunny'
require 'json'
require 'logger'

class RabbitSubscriber

  def initialize(metric_logger, bunny_args)
    @connection = Bunny.new(bunny_args)
    connect(@connection)
    @exchanges = {}
    @queues = {}
    @metric_logger = metric_logger
  end

  def connect(connection)
    attempts ||= 1
    connection.start
  rescue => error
    $logger.error error.message
    if (attempts += 1) < 5
      $logger.warn "Retrying to connect to RabbitMQ in 10 seconds"
      sleep 10
      retry
    else
      raise error
    end
  end

  def rabbitmq_connected?
    @connection && @connection.open?
  end

  def close
    @connection.close
  end

  def setup_queue(exchange_name:, exchange_properties:, queue_name:, queue_properties:)
    channel = @connection.create_channel
    # Exchange and queue properties must match those set up in Camel
    # http://rubybunny.info/articles/exchanges.html
    exchange = channel.direct(exchange_name, exchange_properties)
    @exchanges[exchange_name] = exchange
    # in case message cannot be delivered
    exchange.on_return do |return_info, properties, content|
      $logger.info "Got a returned message: #{content}"
    end

    channel.queue(queue_name, queue_properties).tap do |queue|
      @queues[queue_name] = queue
      # https://www.baeldung.com/java-rabbitmq-exchanges-queues-bindings
      queue.bind(exchange_name, routing_key: queue_name)
    end
  end

  def subscribe_to(exchange_name, queue_name, log_metrics = true)
    begin
      queue = @queues[queue_name]
      $logger.info " [*] Waiting for messages for queue #{queue_name}. To exit press CTRL+C"
      queue.subscribe do |delivery_info, properties, body|
        $logger.debug " [x] #{queue_name}: Received body with size: #{body.size}"
        $logger.debug "reply_to: #{properties.reply_to}"
        $logger.debug "correlation_id: #{properties.correlation_id}"
        $logger.debug "Headers: #{properties.headers}"
        $logger.debug "delivery_info: #{delivery_info}"
        # delivery_info.routing_key is the same as queue_name in this case

        custom_tags = ["queue:#{queue_name}", "app_id:#{properties.app_id || 'unknown'}"]
        start_time = Time.now
        json = JSON.parse(body)
        response = yield(json)
        if json.has_key?('claimNotes')
          custom_tags.append('bgsNoteType:claim')
        elsif json.has_key?('veteranNote')
          custom_tags.append('bgsNoteType:veteran')
        end
      rescue JSON::ParserError => e
        $logger.error e.backtrace
        response = {
          statusCode: 400,
          statusMessage: "BAD_REQUEST",
          messages: [
            {
              key: "#{e.class}",
              severity: "ERROR",
              status: 400,
              text: "#{e.message}",
              timestamp: Time.now.iso8601,
              httpStatus: "BAD_REQUEST"
            }
          ]
        }
      rescue => e
        $logger.error e.backtrace
        response = {
          statusCode: 500,
          statusMessage: "INTERNAL_SERVER_ERROR",
          messages: [
            {
              key: "#{e.class}",
              severity: "ERROR",
              status: 500,
              text: "Unknown Error: #{e.message}",
              timestamp: Time.now.iso8601,
              httpStatus: "INTERNAL_SERVER_ERROR"
            }
          ]
        }
      ensure
        $logger.info "Response: #{response}"
        # delivery_info.exchange or exchange_name can be used
        exchange = @exchanges[exchange_name]
        exchange.publish(
          response.to_json,
          routing_key: properties.reply_to,
          correlation_id: properties.correlation_id
        )

        if log_metrics
          if response[:statusCode] == 200
            @metric_logger.submit_all_metrics(start_time, Time.now, custom_tags)
          else
            @metric_logger.submit_error_metrics(start_time, Time.now, custom_tags)
          end
        end
      end
    rescue Interrupt => _
      @connection.close
      exit(0)
    end
  end
end
