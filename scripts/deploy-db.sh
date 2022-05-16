#!/usr/bin/env bash

cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres-db
  labels:
    app: postgres-db
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres-db
  template:
    metadata:
      labels:
        app: postgres-db
        sidecar.istio.io/inject: "false"
    spec:
      containers:
        - name: postgres-db
          image: postgres
          env:
            - name: POSTGRES_USER
              value: $POSTGRES_USERNAME
            - name: POSTGRES_PASSWORD
              value: $POSTGRES_PASSWORD
            - name: POSTGRES_DB
              value: example
          ports:
            - containerPort: 5432
              protocol: TCP
          resources:
            limits:
              memory: 256Mi
              cpu: 10m
EOF
