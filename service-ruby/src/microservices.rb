#!/usr/bin/env ruby
require_relative 'config/setup'
require 'rabbit_subscriber'
require 'logger'

bunny_args = {
  host: "rabbitmq1",
  user: "guest",
  password: "guest"
}
subscriber = RabbitSubscriber.new(bunny_args)
subscriber.subscribe

puts "Waiting for messages..."
sleep 5.minutes
puts "Time's up! Exiting."
subscriber.close
