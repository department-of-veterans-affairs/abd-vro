#!/usr/bin/env ruby
require_relative 'config/setup'
require 'rabbit_subscriber'
require 'logger'

bunny_args = {
  host: ENV['RABBITMQ_PLACEHOLDERS_HOST'] ||"rabbitmq1",
  user: ENV['RABBITMQ_PLACEHOLDERS_USERNAME'] || "guest",
  password: ENV['RABBITMQ_PLACEHOLDERS_USERPASSWORD'] || "guest"
}
subscriber = RabbitSubscriber.new(bunny_args)
subscriber.subscribe

def listen_for_messages
  while true do
    puts "Waiting for messages..."
    sleep 10.minutes
  end
ensure
  puts "Closing queue subscriptions"
  subscriber.close
end

listen_for_messages
