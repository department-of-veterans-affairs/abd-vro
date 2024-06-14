
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
        @metrics_api = DatadogAPIClient::V2::MetricsAPI.new
    end



    def get_epoch_milliseconds(time = Time.now)
      return (time.to_f * 1000).to_i
    end

    def generate_tags(custom_tags = nil)
      # Similar logic to Java function
      tags = ENV_TAG
      tags << SERVICE_TAG
      tags.concat(custom_tags) if custom_tags
      return tags
    end

    def submit_count(metric, value: 1)
      """
      submit_counts a count metric with by the name 'APP_PREFIX.{metric}'
      :param metric: string containing the metric name
      :param value: value to submit_count by (default: 1)
      """
      full_metric = "#{APP_PREFIX}.#{metric.to_s.downcase}"

      payload = DatadogAPIClient::V2::MetricPayload.new(
        series: [
          DatadogAPIClient::V2::MetricSeries.new(
            metric: full_metric,
            type: DatadogAPIClient::V2::MetricIntakeType::COUNT,
            points: [
              DatadogAPIClient::V2::MetricPoint.new(
                timestamp: Time.now.to_i,
                value: value,
              ),
            ],
            tags: generate_tags(),
          ),
        ],
      )

      begin
        @metrics_api.submit_metrics(payload: payload)
      rescue Exception => e
        $logger.error("Error logging metric: #{full_metric} (count). Error: #{e.class}, Message: #{e.message}")
      end
    end

    def submit_request_duration(start_time, end_time, custom_tags = nil)
      """
      Given start and end seconds, a request duration metric is calculated and the result is submited datadog.

      Args:
          start_time (integer): Request start time in nanoseconds.
          end_time (integer): Request end time in nanoseconds.
          tags (list, optional): A list of custom tags to include. Defaults to nil.
      """

      payload = generate_distribution_metric(
        METRIC[:REQUEST_DURATION],
        Time.now.to_i,
        end_time - start_time,
        custom_tags
      )

      begin
        payload_result = @metrics_api.submit_distribution_points(payload)
        $logger.info(
          "submitted #{payload.series.first.metric}  #{payload_result.status}"
        )
      rescue Exception => e
        $logger.error(
        "exception submitting #{payload.series.first.metric}  #{e.message}"
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
      full_metric = "#{APP_PREFIX}.#{metric.to_s.downcase}"

      payload = DatadogAPIClient::V1::DistributionPointsPayload.new(
        series: [
          DatadogAPIClient::V1::DistributionPointsSeries.new(
            metric: full_metric,
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
        ],
      )
      return payload
    end


    # datadog client dora metrics
    # def deployment_requested
    # DatadogAPIClient.configure do |config|
    #   config.unstable_operations["v2.create_dora_deployment".to_sym] = true
    # end
    # api_instance = DatadogAPIClient::V2::DORAMetricsAPI.new

    # body = DatadogAPIClient::V2::DORADeploymentRequest.new({
    #   data: DatadogAPIClient::V2::DORADeploymentRequestData.new({
    #     attributes: DatadogAPIClient::V2::DORADeploymentRequestAttributes.new({
    #       finished_at: 1693491984000000000,
    #       git: DatadogAPIClient::V2::DORAGitInfo.new({
    #         commit_sha: "66adc9350f2cc9b250b69abddab733dd55e1a588",
    #         repository_url: "https://github.com/organization/example-repository",
    #       }),
    #       service: "shopist",
    #       started_at: 1693491974000000000,
    #       version: "v1.12.07",
    #     }),
    #   }),
    # })
    # p api_instance.create_dora_deployment(body)
end