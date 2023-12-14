# Inspired by the README at https://github.com/ruby-amqp/bunny

require_relative '../config/constants'
require_relative 'before_integration_test'
require_relative 'add_note_tests'

ch = before_integration_test()
test_successful_add_note(ch)

puts "svc-bgs-api integration tests completed"
