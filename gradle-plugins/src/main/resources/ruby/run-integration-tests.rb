# Inspired by the README at https://github.com/ruby-amqp/bunny

require 'bunny'
require 'json'

require_relative '../config/constants'

# Constants used by integration tests
ADD_NOTE_REPLY_QUEUE = "add-note-response"
ADD_NOTE_PAYLOAD = "{\\\"veteranNote\\\":\\\"test\\\",\\\"veteranParticipantId\\\":111}"

conn = Bunny.new(BUNNY_ARGS)
conn.start

ch = conn.create_channel
x = ch.direct(BGS_EXCHANGE_NAME, CAMEL_MQ_PROPERTIES)

r = ch.queue(ADD_NOTE_REPLY_QUEUE, CAMEL_MQ_PROPERTIES).bind(x, :routing_key => ADD_NOTE_REPLY_QUEUE)

q = ch.queue(ADD_NOTE, CAMEL_MQ_PROPERTIES)
q.publish(ADD_NOTE_PAYLOAD, :reply_to => ADD_NOTE_REPLY_QUEUE, :delivery_mode => 1, :correlation_id => ${{env.CORRELATION_ID}}, :payload_encoding => string)

# TODO: check to make sure there is a message in the reply queue before popping

delivery_info, properties, payload = r.pop

# Validate response from the add-note message proceessing

echo "Checking correlation_id..."
echo "correlation_id: ${{ fromJson(steps.addNoteResponse.outputs.response).data[0].properties.correlation_id }}"

if properties.correlation_id != ${{env.CORRELATION_ID}} then
    raise ""

if [ "${{ fromJson(steps.addNoteResponse.outputs.response).data[0].properties.correlation_id }}" != "${{env.CORRELATION_ID}}" ]; then
echo "Unexpected correlation_id: Expected ${{env.CORRELATION_ID}}. Found ${{ fromJson(steps.addNoteResponse.outputs.response).data[0].properties.correlation_id }}"
CHECKS_FAILED=true
fi

echo "Skip checking payload until bgs-api is connected..."

if $CHECKS_FAILED; then
echo "Some checks failed."
exit 10
fi

if JSON.parse(payload)["statusCode"] != 200 then
    raise "svc-bgs-api healthcheck failed"
end
