require_relative '../config/constants'

def before_integration_test()
    conn = Bunny.new(BUNNY_ARGS)
    conn.start

    return conn.create_channel
end
