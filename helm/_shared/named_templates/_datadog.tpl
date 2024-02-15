{{/*
  For DataDog REST API
*/}}

{{- define "vro.datadog.envVars" -}}
- name: DD_SITE
  valueFrom:
    secretKeyRef:
      name: vro-datadog
      key: DD_SITE
- name: DD_API_KEY
  valueFrom:
    secretKeyRef:
      name: vro-datadog
      key: DD_API_KEY
- name: DD_APP_KEY
  valueFrom:
    secretKeyRef:
      name: vro-datadog
      key: DD_APP_KEY
{{- end }}
