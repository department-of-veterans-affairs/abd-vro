import json
import os
import sys
from reclaim_sdk.models.task import ReclaimTask
from datetime import datetime, timedelta
def main():

    current_file = os.path.basename(__file__)
    file_name = os.path.splitext(current_file)[0]
    reviewer = file_name.split('_')[0]

    event_data = dict()
    event_path = sys.argv[1]
    with open(event_path, 'r') as f:
        event_data = json.load(f)

    try:
        create_task(event_data)
        print(f"Reclaim Task for {reviewer} was submitted successfully from: {file_name}")
    except Exception as ex:
        print(f"Reclaim Task for {reviewer} threw an exception while attempting to save Reclaim Task: {ex}")
        sys.exit(10)

def create_task(event_data):
    if "pull_request" not in event_data.keys():
        raise KeyError("event_data is missing required pull_request attribute")

    pr = event_data["pull_request"]
    # used as a context manager, the task will automatically
    # be saved to the API when exiting the context.
    with ReclaimTask() as task:
        task.name = pr.get("title")
        task.description = pr.get("body")
        # All durations are set in hours
        task.duration = 1
        task.min_work_duration = 0.25
        task.max_work_duration = 2
        task.start_date = datetime.now()
        # PR reviews should be completed within 24 hours of the request
        task.due_date = datetime.now() + timedelta(days=1)
        task.is_work_task = True

if __name__ == "__main__":
    main()