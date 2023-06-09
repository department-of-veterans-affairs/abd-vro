{{- define "vro.imagePullSecrets" -}}
imagePullSecrets:
- name: devops-ghcr
{{- end }}

{{- define "vro.imageRegistryPath" -}}
ghcr.io/department-of-veterans-affairs/{{ .Values.global.images.repo }}/{{ .Values.global.imagePrefix }}
{{- end }}

{{- define "vro.containerSuffix" -}}
--{{ .Values.global.environment }}
{{- end }}

{{- define "vro.imageTag" -}}
{{- .Values.imageTag  | default .Values.global.imageTag }}
{{- end }}
