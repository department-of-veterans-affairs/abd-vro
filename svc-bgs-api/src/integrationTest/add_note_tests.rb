# Inspired by the README at https://github.com/ruby-amqp/bunny

require 'bunny'
require 'json'

require_relative '../config/constants'

# Constants used by integration tests
ADD_NOTE_REPLY_QUEUE = "add-note-response"
ADD_NOTE_PAYLOAD = "{\\\"veteranNote\\\":\\\"test\\\",\\\"veteranParticipantId\\\":111}"
CORRELATION_ID = "1234"


def test_successful_add_note(ch)
    x = ch.direct(BGS_EXCHANGE_NAME, CAMEL_MQ_PROPERTIES)
    r = ch.queue(ADD_NOTE_REPLY_QUEUE, CAMEL_MQ_PROPERTIES).bind(x, :routing_key => ADD_NOTE_REPLY_QUEUE)

    q = ch.queue(ADD_NOTE_QUEUE, CAMEL_MQ_PROPERTIES)
    q.publish(ADD_NOTE_PAYLOAD, :reply_to => ADD_NOTE_REPLY_QUEUE, :delivery_mode => 1, :correlation_id => CORRELATION_ID, :payload_encoding => "string")

    delivery_info, properties, payload = r.pop
    while properties == nil
        delivery_info, properties, payload = r.pop
    end

    # Validate response from the add-note message processing
    response_correlation_id = properties.correlation_id

    puts "Checking correlation_id..."
    puts "correlation_id: #{response_correlation_id}"

    if response_correlation_id != CORRELATION_ID then
        raise "Unexpected correlation_id: Expected #{CORRELATION_ID}. Found #{response_correlation_id} "
    end
end
