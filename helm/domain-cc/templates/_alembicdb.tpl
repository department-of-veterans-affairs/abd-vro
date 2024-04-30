{{/*
  For Alembic to connect to Postgres Domain-cc DB schema
*/}}
{{- define "domainCc.alembic.envVars" -}}
- name: DOMAIN_CC_USER
  valueFrom:
    secretKeyRef:
      name: domain-cc-db
      key: DOMAIN_CC_USER
- name: DOMAIN_CC_PW
  valueFrom:
    secretKeyRef:
      name: domain-cc-db
      key: DOMAIN_CC_PW
- name: ALEMBIC_SCHEMA
  value: {{ .Values.global.service.db.domainCcSchemaName }}
{{- end }}
