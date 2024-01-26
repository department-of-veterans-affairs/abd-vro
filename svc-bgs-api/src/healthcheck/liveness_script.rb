# This script initializes a BgsClient instance and calls a method (simple_check_method) to check the application's connection to 
# the BGS service. Given that your application's primary function is to interact with the BGS service, this script directly checks 
# the key functionality of your application. This approach is more aligned with the purpose of a liveness probe, which is to verify 
# that your application is running and able to perform its basic functions. The script is also tailored to the specific operations of 
# your application, making it a more accurate indicator of its liveness. Conclusion: Script 2 is the better choice because it directly 
# tests the core functionality of your application (interaction with BGS), ensuring that the application is not only running but is also 
# capable of performing its intended tasks. Remember to replace simple_check_method with a real method that performs a safe, lightweight 
# check against the BGS service.

require_relative 'rabbit_subscriber'

def check_rabbitmq_connection
  subscriber = RabbitSubscriber.new(BUNNY_ARGS)
  subscriber.connected?
rescue StandardError => e
  puts "Liveness check failed: #{e.message}"
  false
end

exit check_rabbitmq_connection ? 0 : 1
