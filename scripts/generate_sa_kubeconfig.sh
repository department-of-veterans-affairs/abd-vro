#!/bin/bash

namespace=$1
kubeconfig=$2

if [ -z $kubeconfig ] || [ ! -f $kubeconfig ]; then
  kubeconfig=~/.kube/config
fi

export context_name=$(kubectl --kubeconfig ${kubeconfig} config current-context)
cluster=$(kubectl --kubeconfig ${kubeconfig} config view -o json --raw \
  | jq '.clusters[] | select(.name == env.context_name)')
cluster_name=$(echo "${cluster}" | jq .name | tr -d '"')
service_account_name=default
token_name=$(kubectl --kubeconfig ${kubeconfig} -n $namespace -o json get sa $service_account_name \
  | jq '.secrets[0].name' | tr -d '"')
token=$(kubectl --kubeconfig ${kubeconfig} -n $namespace -o json get secret $token_name \
  | jq '.data.token' | tr -d '"' | base64 -d)

cat <<EOF | base64
apiVersion: v1
clusters:
- cluster:
    certificate-authority-data: $(echo "${cluster}" | jq '.cluster."certificate-authority-data"' | tr -d '"')
    server: $(echo "${cluster}" | jq .cluster.server | tr -d '"')
  name: $cluster_name
contexts:
- context:
    cluster: $cluster_name
    namespace: $namespace
    user: $service_account_name
  name: $service_account_name
current-context: $service_account_name
kind: Config
preferences: {}
users:
- name: $service_account_name
  user:
    token: $token
EOF
