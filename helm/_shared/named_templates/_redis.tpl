{{/*
  For clients to connect to Redis
*/}}

{{- define "vro.redisClient.envVars" -}}
- name: REDIS_PLACEHOLDERS_HOST
  value: {{ .Values.global.hostnamePrefix }}-redis
- name: REDIS_PLACEHOLDERS_PASSWORD
  valueFrom:
    secretKeyRef:
      name: vro-redis
      key: REDIS_CLIENTUSER_PASS
{{- end }}
