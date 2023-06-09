{{- define "vro.postgresUrl" -}}
{{- printf "jdbc:postgresql://%s-postgres:%s/%s"
  .Values.global.hostnamePrefix
  (toString .Values.global.service.db.targetPort)
  .Values.global.service.db.databaseName }}
{{- end }}

{{/*
  For clients to connect to DB
*/}}
{{- define "vro.dbClient.envVars" -}}
- name: POSTGRES_URL
  value: {{ include "vro.postgresUrl" . }}
#  valueFrom:
#    secretKeyRef:
#      name: vro-db
#      key: { { .Values.global.postgres.secretKeyRef.urlKey }}
- name: POSTGRES_USER
  valueFrom:
    secretKeyRef:
      name: vro-db
      key: DB_CLIENTUSER_NAME
- name: POSTGRES_PASSWORD
  valueFrom:
    secretKeyRef:
      name: vro-db
      key: DB_CLIENTUSER_PASS
- name: POSTGRES_DB
  value: {{ .Values.global.service.db.databaseName }}
#  valueFrom:
#    secretKeyRef:
#      name: vro-db
#      key: { { .Values.global.postgres.secretKeyRef.dbnameKey }}
- name: POSTGRES_SCHEMA
  value: {{ .Values.global.service.db.schemaName }}
#  valueFrom:
#    secretKeyRef:
#      name: vro-db
#      key: { { .Values.global.postgres.secretKeyRef.schemaKey }}
{{- end }}

{{/*
  For Flyway to connect to set up Postgres DB schema
*/}}
{{- define "vro.flyway.envVars" -}}
- name: FLYWAY_URL
  value: {{ include "vro.postgresUrl" . }}
#  valueFrom:
#    secretKeyRef:
#      name: vro-db
#      key: { { .Values.global.dbinit.secretKeyRef.urlKey }}
- name: FLYWAY_USER
  valueFrom:
    secretKeyRef:
      name: vro-db
      key: DB_FLYWAYUSER_NAME
- name: FLYWAY_PASSWORD
  valueFrom:
    secretKeyRef:
      name: vro-db
      key: DB_FLYWAYUSER_PASS
- name: FLYWAY_SCHEMA
  value: {{ .Values.global.service.db.schemaName }}
#  valueFrom:
#    secretKeyRef:
#      name: vro-db
#      key: { { .Values.global.postgres.secretKeyRef.schemaKey }}
- name: FLYWAY_PLACEHOLDERS_USERNAME
  valueFrom:
    secretKeyRef:
      name: vro-db
      key: DB_CLIENTUSER_NAME
- name: FLYWAY_PLACEHOLDERS_DB_NAME
  value: {{ .Values.global.service.db.databaseName }}
#  valueFrom:
#    secretKeyRef:
#      name: vro-db
#      key: { { .Values.global.postgres.secretKeyRef.dbnameKey }}
- name: FLYWAY_PLACEHOLDERS_SCHEMA_NAME
  value: {{ .Values.global.service.db.schemaName }}
#  valueFrom:
#    secretKeyRef:
#      name: vro-db
#      key: { { .Values.global.postgres.secretKeyRef.schemaKey }}
- name: FLYWAY_PLACEHOLDERS_USER_PASSWORD
  valueFrom:
    secretKeyRef:
      name: vro-db
      key: DB_CLIENTUSER_PASS
{{- end }}
