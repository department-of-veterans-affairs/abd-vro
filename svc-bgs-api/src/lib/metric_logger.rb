
require "datadog_api_client"
require 'logger'
require "time"

require_relative '../config/setup'

APP_PREFIX = 'vro_bgs'

ENV_TAG = "environment:"+ENVIRONMENT
SERVICE_TAG = 'service:vro-svc-bgs-api'
STANDARD_TAGS = [ENV_TAG, SERVICE_TAG]

METRIC = {
  REQUEST_START: :REQUEST_START,
  REQUEST_DURATION: :REQUEST_DURATION,
  RESPONSE_COMPLETE: :RESPONSE_COMPLETE,
  RESPONSE_ERROR: :RESPONSE_ERROR,
  LISTENER_ERROR: :LISTENER_ERROR,
  MESSAGE_CONVERSION_ERROR: :MESSAGE_CONVERSION_ERROR
}

class MetricLogger

    def initialize()
        # Configure Datadog API Client
        # Client Configuration object will use the same environment variables as its python counterpart: https://github.com/DataDog/datadog-api-client-ruby/blob/8d06bd2624cdae4c1e688158e003fb92b367b70f/lib/datadog_api_client/configuration.rb#L244
        DatadogAPIClient.configure do |config|
          config.enable_retry = true
        end
        @distribution_metrics_api = DatadogAPIClient::V1::MetricsAPI.new
        @metrics_api = DatadogAPIClient::V2::MetricsAPI.new
    end

    def generate_tags(custom_tags = nil)
      # Similar logic to Java function
      tags = STANDARD_TAGS.clone
      if custom_tags
        if custom_tags.kind_of?(Array)
            tags.concat(custom_tags)
        else
            tags.concat([custom_tags])
        end
      end

      return tags
    end

    def get_metric_payload(metric, value, custom_tags)

        return DatadogAPIClient::V2::MetricPayload.new(
            series: [
              DatadogAPIClient::V2::MetricSeries.new(
                metric: get_full_metric_name(metric),
                type: DatadogAPIClient::V2::MetricIntakeType::COUNT,
                points: [
                  DatadogAPIClient::V2::MetricPoint.new(
                    timestamp: Time.now.to_i,
                    value: value,
                  ),
                ],
                tags: generate_tags(custom_tags)
              ),
            ],
          )
    end

    def get_full_metric_name(metric)
        return "#{APP_PREFIX}.#{metric.to_s.downcase}"
    end

    def submit_count(metric, custom_tags, value)
      """
      submit_counts a count metric with by the name 'APP_PREFIX.{metric}'
      :param metric: string containing the metric name
      :param custom_tags: list of strings
      :param value: value to submit_count by (default: 1)
      """
      payload = get_metric_payload(metric, custom_tags, value)

      begin
        @metrics_api.submit_metrics(payload: payload)
      rescue Exception => e
        $logger.error("Error logging metric: #{metric} (count). Error: #{e.class}, Message: #{e.message}")
      end
    end

    def submit_count_with_default_values(metric)
          """
          submit_counts a count metric of 1 with by the name 'APP_PREFIX.{metric}'
          """
          submit_count(metric, nil, 1)
    end


    def submit_request_duration(start_time, end_time, custom_tags = nil)
      """
      Given start and end seconds, a request duration metric is calculated and the result is submited datadog.

      Args:
          start_time (integer): Request start time in nanoseconds.
          end_time (integer): Request end time in nanoseconds.
          tags (list, optional): A list of custom tags to include. Defaults to nil.
      """
      metric_full_name = get_full_metric_name(METRIC[:REQUEST_DURATION])

      payload = generate_distribution_metric(
        metric_full_name,
        Time.now.to_i,
        end_time - start_time,
        custom_tags
      )

      begin
        payload_result = @distribution_metrics_api.submit_distribution_points(payload)
        $logger.info(
          "submitted #{payload.series.first.metric}  #{payload_result.status}"
        )
      rescue Exception => e
        $logger.error(
        "exception submitting request duration  #{e.message}"
        )
      end
    end

    def generate_distribution_metric(metric, timestamp, value, custom_tags = nil)
      """
      Creates a datadog distribution points payload for submitting distribution metrics to datadog.

      Args:
          metric (symbol): The metric name (e.g., :REQUEST_DURATION).
          timestamp (integer): The timestamp in milliseconds.
          value (float): The distribution point value.
          tags (list, optional): A list of custom tags to include. Defaults to nil.

      Returns:
          hash: The distribution points payload hash.
      """

      payload = DatadogAPIClient::V1::DistributionPointsPayload.new(
        series: [
          DatadogAPIClient::V1::DistributionPointsSeries.new(
            metric: get_full_metric_name(metric),
            points: [
            [
                      Time.now.to_i,
                      [
                        value
                      ],
                    ],

            ],
            tags: generate_tags(custom_tags),
          ),
        ]
      )
      return payload
    end
end