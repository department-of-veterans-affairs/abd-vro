import sys


def lib_queues(channel):
    """Patch for tests"""
    return channel


module = type(sys)('lib.queues')
module.queue_setup = lib_queues
sys.modules['lib.queues'] = module
