#!/usr/bin/env bash

cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: starterkit-api
  labels:
    app: lighthouse-di-starterkit-java
  annotations:
    app.kubernetes.io/name: lighthouse-di-starterkit-java
    app.kubernetes.io/version: "0.1"
    app.kubernetes.io/owner: Example
    app.kubernetes.io/env: dev
spec:
  replicas: 1
  selector:
    matchLabels:
      app: lighthouse-di-starterkit-java
  template:
    metadata:
      labels:
        app: lighthouse-di-starterkit-java
      annotations:
        app.kubernetes.io/podname: lighthouse-di-starterkit-java
        app.kubernetes.io/podversion: "0.1"
        app.kubernetes.io/podowner: Example
        app.kubernetes.io/podenv: dev
    spec:
      imagePullSecrets:
        - name: lighthouse-di-starterkit-java-ghcr

      initContainers:
        - name: pg-isready
          image: postgres
          command:
            - /bin/sh
            - -c
            - |
              /bin/sh -c "
                until pg_isready -d example -h postgres1 -p 5432; do
                  echo 'Waiting for Postgres DB to be available'
                  sleep 5
                done
              "
          resources:
            limits:
              memory: 256Mi
              cpu: 10m
        - name: flyway-migration
          image: ghcr.io/department-of-veterans-affairs/lighthouse-di-starterkit-java/lighthouse-di-starterkit-java-db-init:${CIRCLE_SHA1:0:7}
          env:
            - name: FLYWAY_USER
              value: $POSTGRES_USERNAME
            - name: FLYWAY_PASSWORD
              value: $POSTGRES_PASSWORD
          resources:
            limits:
              memory: 256Mi
              cpu: 10m

      containers:
        - name: starterkit-api
          image: ghcr.io/department-of-veterans-affairs/lighthouse-di-starterkit-java/lighthouse-di-starterkit-java:${CIRCLE_SHA1:0:7}
          ports:
            - containerPort: 8080
              name: http
              protocol: TCP
            - containerPort: 8081
              name: liveness
              protocol: TCP
            - containerPort: 5005
              name: debug
              protocol: TCP
          livenessProbe:
            httpGet:
              path: /liveness
              port: 8081
            initialDelaySeconds: 120
            periodSeconds: 10
            timeoutSeconds: 10
            failureThreshold: 3
          readinessProbe:
            httpGet:
              path: /readiness
              port: 8081
            initialDelaySeconds: 120
            periodSeconds: 10
            timeoutSeconds: 10
            failureThreshold: 3
          env:
            - name: POSTGRES_DBNAME
              value: example
            - name: SPRING_DATASOURCE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: psql-secret
                  key: username
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: psql-secret
                  key: password
            - name: POSTGRES_HOST
              value: jdbc:postgresql://postgres1:5432/example?user=$POSTGRES_USERNAME&password=$POSTGRES_PASSWORD
            - name: JAVA_OPTS
              value: -Xmx512m
            - name: ZIPKIN_ENABLED
              value: "true"
            - name: OPENTRACING_ZIPKIN_HTTPSENDER_BASEURL
              value: http://zipkin.istio-system.svc.cluster.local:9411
          resources:
            limits:
              memory: 1Gi
              cpu: 500m
EOF
