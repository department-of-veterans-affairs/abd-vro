# This script is used by the K8S Readiness probe to check the availability of the BGS-api service. Specifically, it checks
# the configuration of bgs-api environment and application as well as confirms the service is ready. It also checks the status 
# of RabbitMQ thereby verifying that the BGS-api application is ready to perform its basic functions. 

require 'logger'
require_relative '../lib/rabbit_subscriber'
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

# Function to check if BGS settings are correctly loaded and configured
def check_bgs_configuration
  bgs_settings = SETTINGS['bgs']
  
  # Check if all required BGS settings in the settings.yml file are present
  required_keys = %w[application client_station_id client_username log base_url]
  missing_keys = required_keys.select { |key| bgs_settings[key].nil? || bgs_settings[key].to_s.empty? }
  
  if missing_keys.empty?
    log "BGS configuration loaded successfully."
    true
  else
    log "Missing BGS configuration keys: #{missing_keys.join(', ')}"
    false
  end
end

def perform_readiness_checks
  bgs_config_ok = check_bgs_configuration
  rabbitmq_ok = rabbitmq_connection_active?
  if bgs_config_ok && rabbitmq_ok
    log "Readiness checks passed!"
    return true
  else
    log "Readiness checks failed!"
    return false
  end
end

unless perform_readiness_checks
  exit(1)
end
