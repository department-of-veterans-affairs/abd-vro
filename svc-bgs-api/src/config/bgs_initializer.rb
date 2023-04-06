# frozen_string_literal: true

require 'active_support'

require 'bgs'
require_relative 'setup'

puts 'Configuring BGS...'

BGS.configure do |config|
  settings = SETTINGS["bgs"]
  config.application = settings["application"]
  config.client_ip = Socket.ip_address_list.reject(&:ipv4_loopback?).first.ip_address
  config.client_station_id = settings["client_station_id"]
  config.client_username = settings["client_username"]
  config.log = settings["log"]
  config.logger = config.log ? Logger.new(STDOUT) : nil

  # Rather than fall back on {env}.vba.va.gov if $BGS_BASE_URL is blank, we require $BGS_BASE_URL.
  # $BGS_BASE_URL also determines whether HTTPS is used.
  config.jumpbox_url = ENV["BGS_BASE_URL"]
  config.env = nil
  if config.jumpbox_url.downcase.start_with?("https:")
    config.ssl_cert_file = settings["ssl_cert_file"]
    config.ssl_cert_key_file = settings["ssl_cert_key_file"]
    config.ssl_ca_cert = settings["ssl_ca_cert_file"]
    config.ssl_verify_mode = settings["ssl_verify_mode"] || "none"
  else
    config.ssl_verify_mode = "none"
  end
end
