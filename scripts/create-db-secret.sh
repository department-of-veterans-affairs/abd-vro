#!/usr/bin/env bash

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
metadata:
  name: psql-secret
type: kubernetes.io/basic-auth
stringData:
  username: $POSTGRES_USERNAME
  password: $POSTGRES_PASSWORD
EOF
