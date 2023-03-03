{{/*
  This file contains Named Templates to reduce repetition in template files.
  https://helm.sh/docs/chart_template_guide/named_templates/#partials-and-_-files
*/}}

{{/***************************************************************
  Reusable templates for accessing K8s secrets
*/}}

{{- define "vro.imagePullSecrets" -}}
imagePullSecrets:
- name: {{ .Values.global.images.pullSecretsName }}
{{- end }}

{{- define "vro.imageRegistryPath" -}}
{{- print "ghcr.io/department-of-veterans-affairs/" .Values.global.images.repo }}
{{- end }}

{{- define "vro.postgresUrl" -}}
{{- printf "jdbc:postgresql://%s-postgres:%s/%s"
  .Values.global.hostnamePrefix
  (toString .Values.global.service.db.targetPort)
  .Values.global.service.db.databaseName }}
{{- end }}

{{/***************************************************************
   Ports for services
*/}}


{{/***************************************************************
  Reusable templates for accessing K8s secrets
*/}}

{{- define "vro.valueFromSecret.rabbitmqUser" -}}
valueFrom:
  secretKeyRef:
    name: {{ .Values.global.rabbitmq.secretKeyRef.name }}
    key: {{ .Values.global.rabbitmq.secretKeyRef.usernameKey }}
{{- end }}

{{- define "vro.valueFromSecret.rabbitmqPwd" -}}
valueFrom:
  secretKeyRef:
    name: {{ .Values.global.rabbitmq.secretKeyRef.name }}
    key: {{ .Values.global.rabbitmq.secretKeyRef.passwordKey }}
{{- end }}

{{/***************************************************************
   Credentials for clients to connect to services
*/}}

{{/*
  For clients to connect to MQ
*/}}
{{- define "vro.mqClient.envVars" -}}
- name: RABBITMQ_PLACEHOLDERS_HOST
  value: {{ .Values.global.hostnamePrefix }}-rabbitmq
- name: RABBITMQ_PLACEHOLDERS_USERNAME
  {{- include "vro.valueFromSecret.rabbitmqUser" . | nindent 2 }}
- name: RABBITMQ_PLACEHOLDERS_USERPASSWORD
  {{- include "vro.valueFromSecret.rabbitmqPwd" . | nindent 2 }}
{{- end }}

{{/*
  For clients to connect to Redis
*/}}
{{- define "vro.redisClient.envVars" -}}
- name: REDIS_PLACEHOLDERS_HOST
  value: {{ .Values.global.hostnamePrefix }}-redis
- name: REDIS_PLACEHOLDERS_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.redis.secretKeyRef.name }}
      key: {{ .Values.global.redis.secretKeyRef.passwordKey }}
{{- end }}

{{/*
  For clients to connect to DB
*/}}
{{- define "vro.dbClient.envVars" -}}
- name: POSTGRES_URL
  value: {{ include "vro.postgresUrl" . }}
#  valueFrom:
#    secretKeyRef:
#      name: {{ .Values.global.postgres.secretKeyRef.name }}
#      key: {{ .Values.global.postgres.secretKeyRef.urlKey }}
- name: POSTGRES_USER
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.postgres.secretKeyRef.name }}
      key: {{ .Values.global.postgres.secretKeyRef.usernameKey }}
- name: POSTGRES_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.postgres.secretKeyRef.name }}
      key: {{ .Values.global.postgres.secretKeyRef.passwordKey }}
- name: POSTGRES_DB
  value: {{ .Values.global.service.db.databaseName }}
#  valueFrom:
#    secretKeyRef:
#      name: {{ .Values.global.postgres.secretKeyRef.name }}
#      key: {{ .Values.global.postgres.secretKeyRef.dbnameKey }}
- name: POSTGRES_SCHEMA
  value: {{ .Values.global.service.db.schemaName }}
#  valueFrom:
#    secretKeyRef:
#      name: {{ .Values.global.postgres.secretKeyRef.name }}
#      key: {{ .Values.global.postgres.secretKeyRef.schemaKey }}
{{- end }}

{{/*
  For Flyway to connect to set up Postgres DB schema
*/}}
{{- define "vro.flyway.envVars" -}}
- name: FLYWAY_URL
  value: {{ include "vro.postgresUrl" . }}
#  valueFrom:
#    secretKeyRef:
#      name: {{ .Values.global.dbinit.secretKeyRef.name }}
#      key: {{ .Values.global.dbinit.secretKeyRef.urlKey }}
- name: FLYWAY_USER
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.dbinit.secretKeyRef.name }}
      key: {{ .Values.global.dbinit.secretKeyRef.usernameKey }}
- name: FLYWAY_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.dbinit.secretKeyRef.name }}
      key: {{ .Values.global.dbinit.secretKeyRef.passwordKey }}
- name: FLYWAY_SCHEMA
  value: {{ .Values.global.service.db.schemaName }}
#  valueFrom:
#    secretKeyRef:
#      name: {{ .Values.global.postgres.secretKeyRef.name }}
#      key: {{ .Values.global.postgres.secretKeyRef.schemaKey }}
- name: FLYWAY_PLACEHOLDERS_USERNAME
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.postgres.secretKeyRef.name }}
      key: {{ .Values.global.postgres.secretKeyRef.usernameKey }}
- name: FLYWAY_PLACEHOLDERS_DB_NAME
  value: {{ .Values.global.service.db.databaseName }}
#  valueFrom:
#    secretKeyRef:
#      name: {{ .Values.global.postgres.secretKeyRef.name }}
#      key: {{ .Values.global.postgres.secretKeyRef.dbnameKey }}
- name: FLYWAY_PLACEHOLDERS_SCHEMA_NAME
  value: {{ .Values.global.service.db.schemaName }}
#  valueFrom:
#    secretKeyRef:
#      name: {{ .Values.global.postgres.secretKeyRef.name }}
#      key: {{ .Values.global.postgres.secretKeyRef.schemaKey }}
- name: FLYWAY_PLACEHOLDERS_USER_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.postgres.secretKeyRef.name }}
      key: {{ .Values.global.postgres.secretKeyRef.passwordKey }}
{{- end }}

{{/***************************************************************
    Persistent Volumes
*/}}

{{/*
  EBS Volume for Postgres DB
  Containers using this EBS volume must also use pgdata.affinity below
*/}}
{{- define "vro.volumes.pgdata" -}}
- name: {{ .Values.global.pgdata.pvcName }}
  persistentVolumeClaim:
    claimName: {{ .Values.global.pgdata.pvcName }}
{{- end }}

{{- define "vro.volumeMounts.pgdata" -}}
- name: {{ .Values.global.pgdata.pvcName }}
  mountPath: {{ .Values.global.pgdata.mountPath }}
{{- end }}

{{/*
  EFS Volume mount for tracking API requests
*/}}
{{- define "vro.volumes.tracking" -}}
- name: {{ .Values.global.tracking.pvcName }}
  persistentVolumeClaim:
    claimName: {{ .Values.global.tracking.pvcName }}
{{- end }}

{{- define "vro.volumeMounts.tracking" -}}
- name: {{ .Values.global.tracking.pvcName }}
  mountPath: {{ .Values.global.tracking.mountPath }}
{{- end }}

{{/*
  EBS volumes can only be mounted by containers in the same node.
  This affinity added to pod specs ensures the pod runs on the same node.
  https://kubernetes.io/docs/concepts/scheduling-eviction/assign-pod-node/#inter-pod-affinity-and-anti-affinity
  Remember to also add `.Values.global.pgdata.labels` to spec.template.metadata.labels
*/}}
{{- define "vro.volume.pgdata.affinity" -}}
affinity:
  podAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
    - labelSelector:
        matchExpressions:
        - key: volume
          operator: In
          values:
          - pgdatabase
          # app label value must match the labels in postgres/values.yaml
          # app: vro-postgres
      # https://stackoverflow.com/questions/72240224/what-is-topologykey-in-pod-affinity
      topologyKey: topology.kubernetes.io/zone
{{- end }}
