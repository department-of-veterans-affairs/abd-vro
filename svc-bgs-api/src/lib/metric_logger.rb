require 'datadog_api_client'
require 'logger'
require 'time'

require_relative '../config/constants'
require_relative '../config/setup'

APP_PREFIX = 'vro_bgs'

ENV_TAG = 'environment:' + ENVIRONMENT
SERVICE_TAG = 'service:vro-svc-bgs-api'
STANDARD_TAGS = [ENV_TAG, SERVICE_TAG]

METRIC = {
  REQUEST_START: :REQUEST_START,
  REQUEST_DURATION: :REQUEST_DURATION,
  RESPONSE_COMPLETE: :RESPONSE_COMPLETE,
  RESPONSE_ERROR: :RESPONSE_ERROR
}

class MetricLogger
  def initialize
    # Configure Datadog API Client
    # Client Configuration object will use the same environment variables as its python counterpart: https://github.com/DataDog/datadog-api-client-ruby/blob/8d06bd2624cdae4c1e688158e003fb92b367b70f/lib/datadog_api_client/configuration.rb#L244
    DatadogAPIClient.configure do |config|
      config.enable_retry = true
    end
    @distribution_metrics_api = DatadogAPIClient::V1::MetricsAPI.new
    @metrics_api = DatadogAPIClient::V2::MetricsAPI.new

    begin
        api_instance = DatadogAPIClient::V1::AuthenticationAPI.new
        api_instance.validate()
        $logger.info("Succeeded Datadog authentication check")
    rescue Exception => e
        $logger.error("Failed Datadog authentication check: #{e.message}")
    end
  end

  def generate_tags(custom_tags = nil)
    # Similar logic to Java function
    tags = STANDARD_TAGS.clone
    if custom_tags
      if custom_tags.is_a?(Array)
        tags.concat(custom_tags)
      else
        tags.concat([custom_tags])
      end
    end

    tags
  end

  def get_metric_payload(_metric, _value, _custom_tags)
    DatadogAPIClient::V2::MetricPayload.new({
        series: [
            DatadogAPIClient::V2::MetricSeries.new({
                metric: get_full_metric_name(_metric),
                type: DatadogAPIClient::V2::MetricIntakeType::COUNT,
                points: [
                    DatadogAPIClient::V2::MetricPoint.new({
                    timestamp: Time.now.to_i,
                    value: _value
                    })
                ],
                tags: generate_tags(_custom_tags)
            })
        ]
    })
  end

  def get_full_metric_name(metric)
    "#{APP_PREFIX}.#{metric.to_s.downcase}"
  end

  def submit_count(metric, value, custom_tags)
    payload = get_metric_payload(metric, value, custom_tags)

    begin
      @metrics_api.submit_metrics(payload)
      $logger.info("submitted #{payload.series.first.metric}")
    rescue Exception => e
      $logger.error("Error logging metric: #{metric} (count). Error: #{e.class}, Message: #{e.message}")
    end
  end

  def submit_count_with_default_value(metric, custom_tags)
    submit_count(metric, 1, custom_tags)
  end

  def submit_request_duration(start_time, end_time, custom_tags = nil)
    metric_full_name = get_full_metric_name(METRIC[:REQUEST_DURATION])

    payload = generate_distribution_metric(
      metric_full_name,
      end_time - start_time,
      custom_tags
    )

    begin
      opts = {
        content_encoding: DatadogAPIClient::V1::DistributionPointsContentEncoding::DEFLATE
      }
      payload_result = @distribution_metrics_api.submit_distribution_points(payload, opts)
      $logger.info(
        "submitted #{payload.series.first.metric}  #{payload_result.status}"
      )
    rescue Exception => e
      $logger.error(
        "exception submitting request duration  #{e.message}"
      )
    end
  end

  def generate_distribution_metric(metric, value, custom_tags = nil)
    DatadogAPIClient::V1::DistributionPointsPayload.new({
                                                          series: [
                                                            DatadogAPIClient::V1::DistributionPointsSeries.new({
                                                                                                                 metric: get_full_metric_name(metric),
                                                                                                                 points: [
                                                                                                                   [
                                                                                                                     Time.now.to_i,
                                                                                                                     [
                                                                                                                       value
                                                                                                                     ]
                                                                                                                   ]

                                                                                                                 ],
                                                                                                                 tags: generate_tags(custom_tags)
                                                                                                               })
                                                          ]
                                                        })
  end
end
