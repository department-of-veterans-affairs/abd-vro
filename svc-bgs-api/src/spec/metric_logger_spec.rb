require 'metric_logger'

describe MetricLogger do
  let(:client) { MetricLogger.new }
  let(:api_instance) { double("api_instance") }
  let(:metrics) { double("metrics") }
  let(:distributions) { double("distributions") }

  before do
    allow(DatadogAPIClient::V1::AuthenticationAPI).to receive(:new).and_return(api_instance)
    allow(api_instance).to receive(:validate).and_return(true)
    allow(DatadogAPIClient::V1::MetricsAPI).to receive(:new).and_return(metrics)
    allow(DatadogAPIClient::V2::MetricsAPI).to receive(:new).and_return(distributions)
  end

  it 'generates standard tags when no custom tags are specified' do
    tags = client.generate_tags
    expect(tags).to eq(['environment:local', 'service:vro-svc-bgs-api'])
  end

  it 'adds custom tags to the standard tags' do
    custom_tags = ['color:green', 'animal:frog']
    tags = client.generate_tags(custom_tags)
    expect(tags).to match_array(['color:green', 'animal:frog', 'environment:local', 'service:vro-svc-bgs-api'])
  end

  it 'generates the full metric name with app prefix and lowercase' do
    full_metric_name = client.get_full_metric_name('PURPLE')
    expect(full_metric_name).to eq('vro_bgs.purple')
  end

  it 'generates the full metric name with app prefix and lowercase when given a METRIC' do
    full_metric_name = client.get_full_metric_name(METRIC[:REQUEST_START])
    expect(full_metric_name).to eq('vro_bgs.request_start')
  end

  it 'generates a metric intake payload' do
    # value of MetricIntakeType.COUNT is 1
    # ref: https://github.com/DataDog/datadog-api-client-ruby/blob/4b3bf85/lib/datadog_api_client/v2/models/metric_intake_type.rb#L24
    timestamp_before_getting_payload = Time.now.to_i
    payload = client.get_metric_payload('cats', 42, ['sunny:true', 'humid:false'])
    expect(payload.series.length).to eq(1)
    expect(payload.series[0].type).to eq(1)
    expect(payload.series[0].metric).to eq('vro_bgs.cats')
    expect(payload.series[0].tags).to match_array(['sunny:true', 'humid:false', 'environment:local',
                                                   'service:vro-svc-bgs-api'])
    expect(payload.series[0].points.length).to eq(1)
    expect(payload.series[0].points[0].timestamp).to be_between(timestamp_before_getting_payload, Time.now.to_i)
    expect(payload.series[0].points[0].value).to eq(42)
  end

  it 'generates a metric intake payload when no custom tags are defined' do
    timestamp_before_getting_payload = Time.now.to_i
    payload = client.get_metric_payload('dogs', 51, nil)

    expect(payload.series.length).to eq(1)
    expect(payload.series[0].type).to eq(1)
    expect(payload.series[0].metric).to eq('vro_bgs.dogs')
    expect(payload.series[0].tags).to match_array(['environment:local', 'service:vro-svc-bgs-api'])
    expect(payload.series[0].points.length).to eq(1)
    expect(payload.series[0].points[0].timestamp).to be_between(timestamp_before_getting_payload, Time.now.to_i)
    expect(payload.series[0].points[0].value).to eq(51)
  end

  it 'generates a distribution metric payload' do
    timestamp_before_getting_payload = Time.now.to_i
    payload = client.generate_distribution_metric(METRIC[:REQUEST_DURATION], 424, ['dog.adoptions.ventura'])

    expect(payload.series.length).to eq(1)
    expect(payload.series[0].metric).to eq('vro_bgs.request_duration')
    expect(payload.series[0].tags).to match_array(['dog.adoptions.ventura', 'environment:local',
                                                   'service:vro-svc-bgs-api'])
    expect(payload.series[0].points.length).to eq(1)
    expect(payload.series[0].points[0][0]).to be_between(timestamp_before_getting_payload, Time.now.to_i)
    expect(payload.series[0].points[0][1][0]).to eq(424)
  end

  it 'generates a distribution metric payload when no custom tags are defined' do
    timestamp_before_getting_payload = Time.now.to_i
    payload = client.generate_distribution_metric(METRIC[:REQUEST_DURATION], 424)

    expect(payload.series.length).to eq(1)
    expect(payload.series[0].metric).to eq('vro_bgs.request_duration')
    expect(payload.series[0].tags).to match_array(['environment:local', 'service:vro-svc-bgs-api'])
    expect(payload.series[0].points.length).to eq(1)
    expect(payload.series[0].points[0][0]).to be_between(timestamp_before_getting_payload, Time.now.to_i)
    expect(payload.series[0].points[0][1][0]).to eq(424)
  end
end
