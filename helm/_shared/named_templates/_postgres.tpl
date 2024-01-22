{{- define "vro.postgresUrl" -}}
  valueFrom:
    secretKeyRef:
      name: rds-db
      key: DB_URL
{{- end }}

{{/*
  For clients to connect to DB
*/}}
{{- define "vro.dbClient.envVars" -}}
- name: POSTGRES_URL
  {{ include "vro.postgresUrl" . }}
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
- name: POSTGRES_SCHEMA
  value: {{ .Values.global.service.db.schemaName }}
{{- end }}

{{/*
  For Flyway to connect to set up Postgres DB schema
*/}}
{{- define "vro.flyway.envVars" -}}
- name: FLYWAY_URL
  {{ include "vro.postgresUrl" . }}
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
- name: FLYWAY_PLACEHOLDERS_USERNAME
  valueFrom:
    secretKeyRef:
      name: vro-db
      key: DB_CLIENTUSER_NAME
- name: FLYWAY_PLACEHOLDERS_DB_NAME
  value: {{ .Values.global.service.db.databaseName }}
- name: FLYWAY_PLACEHOLDERS_SCHEMA_NAME
  value: {{ .Values.global.service.db.schemaName }}
- name: FLYWAY_PLACEHOLDERS_USER_PASSWORD
  valueFrom:
    secretKeyRef:
      name: vro-db
      key: DB_CLIENTUSER_PASS
{{- end }}
