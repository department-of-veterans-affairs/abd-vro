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

{{- define "vro.defaultImageTag" -}}
{{ .Values.global.images.tag }}
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
  For clients to connect to DB
*/}}
{{- define "vro.dbClient.envVars" -}}
- name: POSTGRES_URL
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.postgres.secretKeyRef.name }}
      key: {{ .Values.global.postgres.secretKeyRef.urlKey }}
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
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.postgres.secretKeyRef.name }}
      key: {{ .Values.global.postgres.secretKeyRef.dbnameKey }}
- name: POSTGRES_SCHEMA
  valueFrom:
    secretKeyRef:
      name: {{ .Values.global.postgres.secretKeyRef.name }}
      key: {{ .Values.global.postgres.secretKeyRef.schemaKey }}
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

{{/***************************************************************
    Persistent Volumes
*/}}

{{/*
  Volume for Postgres DB
*/}}
{{- define "vro.volumes.pgdata" -}}
- name: {{ .Values.global.pgdata.pvcName }}
  persistentVolumeClaim:
    claimName: {{ .Values.global.pgdata.claimName }}
{{- end }}

{{- define "vro.volumeMounts.pgdata" -}}
- name: {{ .Values.global.pgdata.pvcName }}
  mountPath: /var/lib/postgresql/data/pgdata
{{- end }}

{{/*
  Volume mount for tracking API requests
*/}}
{{- define "vro.volumes.tracking" -}}
- name: {{ .Values.global.tracking.pvcName }}
  persistentVolumeClaim:
    claimName: {{ .Values.global.tracking.claimName }}
{{- end }}

{{- define "vro.volumeMounts.tracking" -}}
- name: {{ .Values.global.tracking.pvcName }}
  mountPath: /persist/tracking
{{- end }}
