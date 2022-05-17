allow {
  input.method == "GET"
  startswith(input.path, "/v1/example/claimsubmissions")
#  input.subject.authorities[_] == "ROLE_HR"
}

allow {
  input.method == "PUT"
  startswith(input.path, "/v1/example/claimsubmissions")
#  input.subject.authorities[_] == "ROLE_ADMIN"
}

allow {
  input.method == "POST"
  startswith(input.path, "/v1/example/claimsubmissions")
#  input.subject.authorities[_] == "ROLE_ADMIN"
}

allow {
  input.method == "DELETE"
  startswith(input.path, "/v1/example/claimsubmissions")
#  input.subject.authorities[_] == "ROLE_ADMIN"
}
