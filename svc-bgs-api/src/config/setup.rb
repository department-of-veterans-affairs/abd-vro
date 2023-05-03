# frozen_string_literal: true

require 'active_support'

# https://www.cloudbees.com/blog/writing-microservice-in-ruby
# This is the file that we’ll always load initially (even in the spec_helper.rb),
# so it’s desirable that it doesn’t do too much (or your tests will be slow).

# https://blog.eq8.eu/til/ruby-logs-and-puts-not-shown-in-docker-container-logs.html
def redirect_output(prefix = nil)
  $fdio_prefix = prefix
  fd = IO.sysopen("/proc/1/fd/1", "w")
  $fdio = IO.new(fd, "w")
  $fdio.sync = true # send log message immediately
  $fdio.puts "Docker should see this `puts` output"
  def puts(args)
    $fdio.puts("#{$fdio_prefix} #{args}")
  end
  $fdio
end

if(ENV['DOCKER_LOGS'] == '1' && File.exist?('/proc/1/fd/1'))
  redirect_output("| ")
end

puts "Running setup.rb"


# so we can henceforth require gems by name
require 'bundler/setup'
# Needed to include prawn-markup gem identified by ref in Gemfile
Bundler.require(:default)

# add the lib folder of our service to the load path
lib_path = File.expand_path '../../lib', __FILE__
$LOAD_PATH.unshift lib_path

ENVIRONMENT = ENV['ENV'].presence || 'local'
puts "Using environment #{ENVIRONMENT}..."

require 'yaml'
# some settings (like the RabbitMQ connection options or some API keys for the services we use)
# that depend on the environment, so load a YAML file and put into a global variable.
settings_file = File.expand_path '../settings.yml', __FILE__
SETTINGS = YAML.load_file(settings_file)[ENVIRONMENT]

if %w(development test).include? ENVIRONMENT
  # Worried about the speed of byebug (it does take a moment)? comment out and only include when explicitly needed
  # require 'byebug'
end

require_relative 'bgs_initializer'

puts 'setup.rb done'
