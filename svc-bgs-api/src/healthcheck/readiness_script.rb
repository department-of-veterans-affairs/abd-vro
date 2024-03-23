# This script is used by the K8S Readiness probe to check the availability of the BIS-api service. Specifically, it checks
# both RabbitMQ connectivity and fetches specific data (vro_participant_id) to confirm the BIS-Api service is ready to perform its basic functions. 

require 'logger'
require_relative '../lib/rabbit_subscriber'
require_relative '../lib/bgs_client'
require_relative '../config/constants'
require_relative '../config/setup'

# Initializes logger
$logger = Logger.new(STDOUT)
$logger.level = Logger::INFO
$logger.info('Logger initialized')

def log(message)
  $logger.info("[Readiness Check]: #{message}")
end

def rabbitmq_connection_active?
  begin
    subscriber = RabbitSubscriber.new(BUNNY_ARGS)
    connected = subscriber.rabbitmq_connected?
    log("RabbitMQ connectivity check: #{connected ? 'Success' : 'Failure'}")
    connected
  rescue => e
    log("RabbitMQ check failed: #{e.message}")    
    false
  end
end

# Function to fetch the VRO participant ID from BGS
def check_vro_participant_id
  bgs_client = BgsClient.new
  participant_id = bgs_client.vro_participant_id
  if participant_id
    log "Successfully fetched VRO participant ID"
    true
  else
    log "Failed to fetch VRO participant ID"
    false
  end
end

def perform_readiness_checks
  rabbitmq_ok = rabbitmq_connection_active?
  vro_participant_id_ok = check_vro_participant_id
  if rabbitmq_ok && vro_participant_id_ok
    log "Readiness checks passed!"
    true
  else
    log "Readiness checks failed!"
    false
  end
end

unless perform_readiness_checks
  exit(1)
end
