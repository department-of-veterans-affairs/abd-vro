# This script is used by the K8S Liveness probe to check the connection between RabbitMQ and BGS-api thereby verifying
# that the BGS-api application is running and able to perform its basic functions.

require 'logger'
require 'bunny'
require_relative '../config/constants'

# Initializes logger
$logger = Logger.new(STDOUT)
$logger.level = Logger::INFO
$logger.info('Logger initialized')

def log(message)
  $logger.info("[Liveness Check]: #{message}")
end

def check_rabbitmq_connection
  begin
    connection = Bunny.new(BUNNY_ARGS)
    connection.start
    connected = connection&.open?
    log "RabbitMQ connectivity check: #{connected ? 'Success' : 'Failure'}"
    connected
  rescue => e
    log "RabbitMQ check failed: #{e.message}"
    false
  ensure
    connection&.close
  end
end

if check_rabbitmq_connection
  log "Liveness check passed!"
  exit(0)
else
  log "Liveness check failed!"
  exit(1)
end
