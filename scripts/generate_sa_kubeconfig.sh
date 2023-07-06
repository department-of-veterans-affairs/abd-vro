#!/bin/bash

namespace=$1
kubeconfig=$2

if [ -z "$kubeconfig" ] || [ ! -f "$kubeconfig" ]; then
  kubeconfig=~/.kube/config
fi
#echo "kubectl --kubeconfig ${kubeconfig} config current-context"
context_name=$(kubectl --kubeconfig "${kubeconfig}" config current-context)
echo context_name "$context_name"
cluster=$(kubectl --kubeconfig "${kubeconfig}" config view -o json --raw | jq --arg context_name 'ldx-nonprod-1' '.clusters[] | select(.name == $context_name)')
echo cluster "${cluster}"
cluster_name=$(echo "${cluster}" | jq .name | tr -d '"')
echo cluster_name "$cluster_name"
service_account_name=default
token_name=$(kubectl --kubeconfig "${kubeconfig}" -n "$namespace" -o json get sa $service_account_name | jq '.secrets[0].name' | tr -d '"')
echo token_name "$token_name"
token=$(kubectl --kubeconfig "${kubeconfig}" -n "$namespace" -o json get secret "$token_name" | jq '.data.token' | tr -d '"' | base64 -d)
echo token "$token"

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
