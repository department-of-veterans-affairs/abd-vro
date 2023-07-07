{{/*
  This file contains Named Templates to reduce repetition in template files.
  https://helm.sh/docs/chart_template_guide/named_templates/#partials-and-_-files
*/}}

{{- define "vro.commonEnvVars" -}}
- name: ENV
  value: {{ required "Expecting dev, prod, etc" .Values.global.environment }}
{{- end }}


{{- define "vro.annotations.pod" -}}
# Don't add annotations that change frequently (like global.commitSha) as it will cause the pod to be updated
vro/environment: {{ .Values.global.environment }}
vro/image-repo: {{ .Values.global.images.repo }}
# annotations is a map[string] to string values; print and quote it in case it's a number
vro/image-tag: {{ include "vro.imageTag" . | print | quote }}
{{- end }}

# Needed so that specifying image tag `latest` with this deploy-time will cause
# K8s to pull the latest image in the last minute -- https://stackoverflow.com/a/69716083
{{- define "vro.annotations.reloadLatest" -}}
{{- if eq "latest" (.Values.imageTag  | default .Values.global.imageTag) }}
vro/deploy-time: {{ now | date "2006-01-02T15:04" }}
{{- end }}
{{- end }}
