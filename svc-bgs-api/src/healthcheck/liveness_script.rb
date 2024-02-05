# This script is used by the K8S Liveness probe to check the connection between RabbitMQ and BGS-api thereby verifying 
# that the BGS-api application is running and able to perform its basic functions. 

require 'logger'
require_relative '../lib/rabbit_subscriber'
require_relative '../config/constants'

# Initializes logger
$logger = Logger.new(STDOUT)
$logger.level = Logger::INFO
$logger.info('Logger initialized')

def log(message)
  $logger.info("[Liveness Check]: #{message}")
end

def check_rabbitmq_connection
  subscriber = RabbitSubscriber.new(BUNNY_ARGS)
  connected = subscriber.rabbitmq_connected?
  log "RabbitMQ connectivity check: #{connected ? 'Success' : 'Failure'}"
  connected
  rescue => e
    log "RabbitMQ check failed: #{e.message}"
    false
  end

if check_rabbitmq_connection
  log "Liveness check passed!"
  exit(0)
else
  log "Liveness check failed!"
  exit(1)
end
