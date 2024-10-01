require 'datadog_api_client'
require 'logger'
require 'time'
require 'async'

require_relative '../config/setup'

APP_PREFIX = 'vro_bgs'

STANDARD_TAGS = [
  'env:' + ENVIRONMENT,
  'team:va-abd-rrd',
  'itportfolio:benefits-delivery',
  'service:vro-svc-bgs-api',
  'dependency:bgs'
]

METRIC = {
  REQUEST_START: :REQUEST_START,
  REQUEST_DURATION: :REQUEST_DURATION,
  RESPONSE_COMPLETE: :RESPONSE_COMPLETE,
  RESPONSE_ERROR: :RESPONSE_ERROR,
  ERROR_DURATION: :ERROR_DURATION
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
      api_instance.validate
    rescue Exception => e
      $logger.warn("event=failedDatadogAuthentication error=#{e.message}")
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

  def get_metric_payload(metric, value, timestamp, custom_tags)
    DatadogAPIClient::V2::MetricPayload.new({
      series: [
        DatadogAPIClient::V2::MetricSeries.new({
             metric: metric,
             type: DatadogAPIClient::V2::MetricIntakeType::COUNT,
             points: [
               DatadogAPIClient::V2::MetricPoint.new({
                   timestamp: timestamp.to_i,
                   value: value
                 })
             ],
             tags: generate_tags(custom_tags)
           })
      ]
    })
  end

  def get_full_metric_name(metric)
    "#{APP_PREFIX}.#{metric.to_s.downcase}"
  end

  def submit_count(metric, value, timestamp, custom_tags)
    metric_name = get_full_metric_name(metric)
    payload = get_metric_payload(metric_name, value, timestamp, custom_tags)

    Async do |task|
      task.async do
        begin
          @metrics_api.submit_metrics(payload)
          $logger.debug("event=countMetricSubmitted metric=#{metric_name} type=COUNT")
        rescue Exception => e
          $logger.warn("event=countMetricFailed metric=#{metric_name} type=COUNT error=#{e.class} message='#{e.message}'")
        end
      end
    end

    nil
  end

  def submit_request_duration(metric, start_time, end_time, custom_tags)
    metric_name = get_full_metric_name(metric)

    duration = end_time - start_time
    payload = generate_distribution_metric(
      metric_name,
      duration,
      custom_tags
    )

    Async do |task|
      task.async do
        begin
          opts = {
            content_encoding: DatadogAPIClient::V1::DistributionPointsContentEncoding::DEFLATE
          }
          @distribution_metrics_api.submit_distribution_points(payload, opts)
          $logger.debug("event=durationMetricSubmitted metric=#{metric_name} type=DISTRIBUTION duration=#{duration}")
        rescue Exception => e
          $logger.warn("event=durationMetricFailed metric=#{metric_name} type=DISTRIBUTION duration=#{duration} error=#{e.class} message='#{e.message}'")
        end
      end
    end

    nil
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

  def submit_error(start_time, end_time, custom_tags)
    submit_count(METRIC[:REQUEST_START], 1, start_time, custom_tags)
    submit_count(METRIC[:RESPONSE_ERROR], 1, end_time, custom_tags)
    submit_request_duration(METRIC[:ERROR_DURATION], start_time, end_time, custom_tags)
  end

  def submit_all_metrics(start_time, end_time, custom_tags)
    submit_count(METRIC[:REQUEST_START], 1, start_time, custom_tags)
    submit_count(METRIC[:RESPONSE_COMPLETE], 1, end_time, custom_tags)
    submit_request_duration(METRIC[:REQUEST_DURATION], start_time, end_time, custom_tags)
  end
end
