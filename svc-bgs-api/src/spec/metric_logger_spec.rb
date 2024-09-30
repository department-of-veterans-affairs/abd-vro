require 'metric_logger'

describe MetricLogger do
  let(:client) { MetricLogger.new }
  let(:api_instance) { double("api_instance") }
  let(:metrics) { double("metrics") }
  let(:distributions) { double("distributions") }
  let(:expected_tags) { STANDARD_TAGS }

  before do
    allow(DatadogAPIClient::V1::AuthenticationAPI).to receive(:new).and_return(api_instance)
    allow(api_instance).to receive(:validate).and_return(true)
    allow(DatadogAPIClient::V1::MetricsAPI).to receive(:new).and_return(metrics)
    allow(DatadogAPIClient::V2::MetricsAPI).to receive(:new).and_return(distributions)
  end

  it 'generates standard tags when no custom tags are specified' do
    tags = client.generate_tags
    expect(tags).to include(*expected_tags)
  end

  it 'adds custom tags to the standard tags' do
    custom_tags = ['color:green', 'animal:frog']
    tags = client.generate_tags(custom_tags)
    expect(tags).to include(*['color:green', 'animal:frog'])
    expect(tags).to include(*expected_tags)
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
    time = Time.now
    payload = client.get_metric_payload('cats', 42, time, ['sunny:true', 'humid:false'])
    expect(payload.series.length).to eq(1)
    expect(payload.series[0].type).to eq(1)
    expect(payload.series[0].metric).to eq('cats')
    expect(payload.series[0].tags).to include(*['sunny:true', 'humid:false'])
    expect(payload.series[0].tags).to include(*expected_tags)
    expect(payload.series[0].points.length).to eq(1)
    expect(payload.series[0].points[0].value).to eq(42)
    expect(payload.series[0].points[0].timestamp).to eq(time.to_i)
  end

  it 'generates a metric intake payload when no custom tags are defined' do
    time = Time.now

    payload = client.get_metric_payload('dogs', 51, time, nil)

    expect(payload.series.length).to eq(1)
    expect(payload.series[0].type).to eq(1)
    expect(payload.series[0].metric).to eq('dogs')
    expect(payload.series[0].tags).to include(*expected_tags)
    expect(payload.series[0].tags.count).to eq(expected_tags.count)
    expect(payload.series[0].points.length).to eq(1)
    expect(payload.series[0].points[0].value).to eq(51)
    expect(payload.series[0].points[0].timestamp).to eq(time.to_i)
  end

  it 'generates a distribution metric payload' do
    timestamp_before_getting_payload = Time.now.to_i
    payload = client.generate_distribution_metric(METRIC[:REQUEST_DURATION], 424, ['dog.adoptions.ventura'])

    expect(payload.series.length).to eq(1)
    expect(payload.series[0].metric).to eq('vro_bgs.request_duration')
    expect(payload.series[0].tags).to include('dog.adoptions.ventura')
    expect(payload.series[0].tags).to include(*expected_tags)
    expect(payload.series[0].points.length).to eq(1)
    expect(payload.series[0].points[0][0]).to be_between(timestamp_before_getting_payload, Time.now.to_i)
    expect(payload.series[0].points[0][1][0]).to eq(424)
  end

  it 'generates a distribution metric payload when no custom tags are defined' do
    timestamp_before_getting_payload = Time.now.to_i
    payload = client.generate_distribution_metric(METRIC[:REQUEST_DURATION], 424)

    expect(payload.series.length).to eq(1)
    expect(payload.series[0].metric).to eq('vro_bgs.request_duration')
    expect(payload.series[0].tags).to include(*expected_tags)
    expect(payload.series[0].tags.count).to eq(expected_tags.count)
    expect(payload.series[0].points.length).to eq(1)
    expect(payload.series[0].points[0][0]).to be_between(timestamp_before_getting_payload, Time.now.to_i)
    expect(payload.series[0].points[0][1][0]).to eq(424)
  end
end
