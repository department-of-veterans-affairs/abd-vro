#!/usr/bin/env ruby
require 'bunny'
require 'json'

def redirect_output(prefix = nil)
  $fdio_prefix = prefix
  fd = IO.sysopen("/proc/1/fd/1", "w")
  $fdio = IO.new(fd, "w")
  $fdio.sync = true # send log message immediately
  $fdio.puts "Docker should see this `puts` output"
  def puts(args)
    $fdio.puts($fdio_prefix + args)
  end
  $fdio
end

if(ENV['DOCKER_LOGS'] && File.exist?('/proc/1/fd/1'))
  redirect_output("| ")
end
puts "processor_c listening"

bunny_args = {
  host: "rabbitmq1",
  user: "guest",
  password: "guest"
}
connection = Bunny.new(bunny_args)

def connect(connection)
  attempts ||= 1
  connection.start
rescue => error
  puts error.message
  if (attempts += 1) < 5
    puts "Retrying to connect to RabbitMQ in 10 seconds"
    sleep 10
    retry
  else
    raise error
  end
end
connect(connection)

channel = connection.create_channel

# Exchange and queue properties must match those set up in Camel
ex = channel.exchange('claimTypeC', type: 'direct',
    durable: true, auto_delete: true)
queue = channel.queue('processor_c.rb',
    durable: true, auto_delete: true)
channel.queue_bind('processor_c.rb', exchange='claimTypeC')

begin
  puts 'Waiting for messages. To exit press CTRL+C'
  queue.subscribe(block: true) do |_delivery_info, properties, body|
    puts " [x] Received #{body}"
    # puts " => #{body.pack('C*')}"
    response = {
      "createdAt" => 1644643358000,
      "submission_id" => "subm123",
      "resultStatus" => "SUCCESS",
      "results" => {
        "bp_diastolic" => 75,
        "rrd_pdf_path" => "rrd/hypertension/subm123.pdf",
        "p_systolic" => 100
      }
    }
    puts "Response: #{response}"
    # response.bytes
    ex.publish(
      response.to_json,
      routing_key: properties.reply_to,
      correlation_id: properties.correlation_id
    )
  end
rescue Interrupt => _
  connection.close

  exit(0)
end
