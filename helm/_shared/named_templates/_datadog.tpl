{{/*
  For DataDog REST API
*/}}

{{- define "vro.datadog.envVars" -}}
- name: DATADOG_API_KEY
  valueFrom:
    secretKeyRef:
      name: vro-datadog
      key: DATADOG_API_KEY
- name: DATADOG_API_KEY_ID
  valueFrom:
    secretKeyRef:
      name: vro-datadog
      key: DATADOG_API_KEY_ID
{{- end }}