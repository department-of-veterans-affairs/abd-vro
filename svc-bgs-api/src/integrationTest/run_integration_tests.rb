# Inspired by the README at https://github.com/ruby-amqp/bunny

require_relative '../config/constants'
require_relative 'before_integration_test'
require_relative 'add_note_tests'

ch = before_integration_test()

puts "Running svc-bgs-api integration tests:"

begin
  test_successful_add_note_via_veteran(ch)
  puts "\ttest_successful_add_note_via_veteran PASSED"
rescue => e
  puts "\ttest_successful_add_note_via_veteran FAILED: #{e}"
  raise e
end

begin
  test_successful_add_note_via_claim(ch)
  puts "\ttest_successful_add_note_via_claim PASSED"
rescue => e
  puts "\ttest_successful_add_note_via_claim FAILED: #{e}"
  raise e
end
