import sys

import data_model


def data_model_validate_request_body(data):
    """Patch for tests"""

    return data_model.validate_request_body(data)


module = type(sys)('data_model')
module.validate_request_body = data_model_validate_request_body
sys.modules['data_model'] = module


def lib_queues(channel):
    """Patch for tests"""
    return channel


module = type(sys)('lib.queues')
module.queue_setup = lib_queues
sys.modules['lib.queues'] = module
