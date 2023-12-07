require_relative '../config/constants'

conn = Bunny.new(BUNNY_ARGS)
conn.start

ch = conn.create_channel
x = ch.direct(BGS_EXCHANGE_NAME, CAMEL_MQ_PROPERTIES)
