import sys

import data_model
import utils


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


def extract_date(date):
    """Patch for tests"""
    return utils.extract_date(date)


def format_date(date):
    """Patch for tests"""
    return utils.format_date(date)


def docs_without_annotations_ids(date):
    """Patch for tests"""
    return utils.docs_without_annotations_ids(date)


module = type(sys)('utils')
module.docs_without_annotations_ids = docs_without_annotations_ids
module.extract_date = extract_date
module.format_date = format_date
sys.modules['utils'] = module
