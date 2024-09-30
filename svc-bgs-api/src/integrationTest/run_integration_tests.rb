# Inspired by the README at https://github.com/ruby-amqp/bunny

require_relative '../config/constants'
require_relative 'before_integration_test'
require_relative 'add_note_tests'

ch = before_integration_test()

puts "Running svc-bgs-api integration tests"
test_successful_add_note_via_veteran(ch)
test_successful_add_note_via_claim(ch)

puts "All tests passed"
