require 'logger'
require 'active_support'
require 'time'

require_relative 'config/setup'
require_relative 'config/constants'

require 'rabbit_subscriber'
require 'metric_logger'
require 'bgs_client'

$stdout.sync = true

def initialize_subscriber(bgs_client, metric_logger)
  subscriber = RabbitSubscriber.new(BUNNY_ARGS)

  # Setup queue/subscription for healthcheck
  # Expects to receive a valid json-encoded message of any structure
  # Returns a json-encoded message with field "statusCode" set to 200
  subscriber.setup_queue(exchange_name: BGS_EXCHANGE_NAME, queue_name: HEALTHCHECK_QUEUE)
  subscriber.subscribe_to(BGS_EXCHANGE_NAME, HEALTHCHECK_QUEUE) do |json|
    $logger.info "Subscriber received healthcheck request #{json}"
    { statusCode: 200 }
  end
  subscriber

  # Setup queue/subscription for BGS
  # Expects to receive a json-encoded message which follows either of two schemas:
  # 1. a "vbmsClaimId" field of a numeric type and a field "claimNotes" set to an array of notes where each note follows
  #    the structure described at
  #    https://github.com/department-of-veterans-affairs/bgs-ext/blob/master/lib/bgs/services/development_notes.rb#L24
  # 2. a "veteranParticipantId" field of a numeric type and a field "veteranNote" which is set to an Object which again
  #    follows the structure defined in the link above
  # See lib/bgs_client#handle_request to gain more understanding of the required fields in the request
  #
  # To understand the expected response visit
  # https://dvagov.sharepoint.com/:w:/r/sites/OITEPMOIAS/dmdocsite/Legacy%20Systems%20Modernization%20LSM/LSM%20Product/Share%20(SHARE)%20-%20%231589/Benefits%20Gateway%20Services%20(BGS)%20Web%20Services.doc?d=w9e5c89b6cc71432ca4f8800921852e24&csf=1&web=1&e=0LEgjt
  # Note this information requires VA-network access
  subscriber.setup_queue(exchange_name: BGS_EXCHANGE_NAME, queue_name: ADD_NOTE_QUEUE)
  subscriber.subscribe_to(BGS_EXCHANGE_NAME, ADD_NOTE_QUEUE) do |json|
    $logger.info "Subscriber received request #{json}"
    begin
      bgs_client.handle_request(json)
    rescue => e
      status_code = e.is_a?(ArgumentError) ? 400 : 500
      status_str = e.is_a?(ArgumentError) ? "BAD_REQUEST" : "INTERNAL_SERVER_ERROR"
      {
        statusCode: status_code,
        statusMessage: status_str,
        messages: [
          {
            key: "#{e.class}",
            severity: "ERROR",
            status: status_code,
            text: "#{e.message}",
            timestamp: Time.now.iso8601,
            httpStatus: status_str
          }
        ]
      }
      begin
        metric_logger.submit_count(METRIC[:RESPONSE_ERROR])
      rescue => metric_e
        $logger.error "Exception submitting metric RESPONSE_ERROR #{e.message}"
      end
    else
      {
        statusCode: 200,
        statusMessage: "OK"
      }
    end
  end
  subscriber
end

def run(subscriber)
  begin
    while true do
      $logger.info "Waiting for messages..."
      sleep 10.minutes
    end
  ensure
    $logger.info "Closing queue subscriptions"
    subscriber.close
  end
end

$logger.info "Initializing subscriber..."
subscriber = initialize_subscriber(BgsClient.new, MetricLogger.new)
$logger.info "Initialized subscriber!"
run(subscriber)
