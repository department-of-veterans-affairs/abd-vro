# Inspired by the README at https://github.com/ruby-amqp/bunny

require 'bunny'
require 'json'

require_relative '../config/constants'

# Constants used by integration tests
ADD_NOTE_REPLY_QUEUE = "svc_bgs_api.add_note_response"
CORRELATION_ID = "1234"


def test_successful_add_note_via_veteran(ch)
    puts "test_successful_add_note_via_veteran"
    x = ch.direct(REQUESTS_EXCHANGE, EXCHANGE_PROPERTIES)
    r = ch.queue(ADD_NOTE_REPLY_QUEUE, { durable: true, auto_delete: true }).bind(x, :routing_key => ADD_NOTE_REPLY_QUEUE)

    q = ch.queue(ADD_NOTE_QUEUE, ADD_NOTE_QUEUE_PROPERTIES)
    q.publish("{\"veteranNote\":\"test\",\"veteranParticipantId\":111}",
              :reply_to => ADD_NOTE_REPLY_QUEUE,
              :delivery_mode => 1,
              :correlation_id => CORRELATION_ID,
              :payload_encoding => "string")

    delivery_info, properties, payload = r.pop
    while properties == nil
        delivery_info, properties, payload = r.pop
    end

    # Validate response from the add-note message processing
    response_correlation_id = properties.correlation_id

    raise "Unexpected correlation_id: Expected #{CORRELATION_ID}. Found #{response_correlation_id} " if response_correlation_id != CORRELATION_ID
end

def test_successful_add_note_via_claim(ch)
    puts "test_successful_add_note_via_claim"
    x = ch.direct(REQUESTS_EXCHANGE, EXCHANGE_PROPERTIES)
    r = ch.queue(ADD_NOTE_REPLY_QUEUE, { durable: true, auto_delete: true }).bind(x, :routing_key => ADD_NOTE_REPLY_QUEUE)

    q = ch.queue(ADD_NOTE_QUEUE, ADD_NOTE_QUEUE_PROPERTIES)
    q.publish("{\"vbms_claim_id\":1234,\"claim_notes\":[\"testNote\"]}",
              :reply_to => ADD_NOTE_REPLY_QUEUE,
              :delivery_mode => 1,
              :correlation_id => CORRELATION_ID,
              :payload_encoding => "string")
    delivery_info, properties, payload = r.pop
    while properties == nil
        delivery_info, properties, payload = r.pop
    end

    # Validate response from the add-note message processing
    response_correlation_id = properties.correlation_id

    raise "Unexpected correlation_id: Expected #{CORRELATION_ID}. Found #{response_correlation_id} " if response_correlation_id != CORRELATION_ID
end
