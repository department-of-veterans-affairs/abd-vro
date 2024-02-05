# This script is used by the K8S Startup probe to check the availability of the BGS-api service. Specifically, it checks
# the configuration of bgs-api environment and application and prevents liveness probe to be starting early.  

require 'logger'
require_relative '../config/setup'

# Initializes logger
$logger = Logger.new(STDOUT)
$logger.level = Logger::INFO
$logger.info('Logger initialized')

def log(message)
  $logger.info("[Startup probe Check]: #{message}")
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
  
  def perform_startup_checks
    bgs_config_ok = check_bgs_configuration
    if bgs_config_ok
      log "Startup probe checks passed!"
      true
    else
      log "Startup probe checks failed!"
      false
    end
  end
 
  unless perform_startup_checks
    exit(1)
  end