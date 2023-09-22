from pika import ConnectionParameters, PlainCredentials


def create_connection_parameters(config) -> ConnectionParameters:
    credentials = PlainCredentials(config["username"], config["password"])
    return ConnectionParameters(
        host=config['host'],
        port=config['port'],
        credentials=credentials)
