import sys
import json

def main():
    event_path = sys.argv[1]
    with open(event_path, 'r') as f:
        event_data = json.load(f)

    # Now event_data is a dictionary containing all the GitHub event details
    reviewer = event_data["review"]["reviewers"][0]["login"]

    print(f"Greetings {reviewer}! This is the default reviewer task, \
but you can implement your very own custom reviewer task by \
adding a .github/reviewer-tasks/{reviewer}_task.py python script .")

if __name__ == "__main__":
    main()