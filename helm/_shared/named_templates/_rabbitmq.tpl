
{{/***************************************************************
  Reusable templates for accessing K8s secrets
*/}}

{{- define "vro.valueFromSecret.rabbitmqUser" -}}
valueFrom:
  secretKeyRef:
    name: vro-mq
    key: MQ_CLIENTUSER_NAME
{{- end }}

{{- define "vro.valueFromSecret.rabbitmqPwd" -}}
valueFrom:
  secretKeyRef:
    name: vro-mq
    key: MQ_CLIENTUSER_PASS
{{- end }}

{{/***************************************************************
   Credentials for clients to connect to the MQ service
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
