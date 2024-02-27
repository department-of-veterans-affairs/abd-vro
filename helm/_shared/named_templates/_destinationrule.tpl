# See the wiki page at https://github.com/department-of-veterans-affairs/abd-vro/wiki/DestinationRule-for-Preventing-Forced-HTTP2-Upgrade
# for context on why this is needed

{{- define "vro.destinationrule" -}}
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: http-{{ .Values.serviceNameSuffix }}
  namespace: va-abd-rrd-{{.Values.global.environment}}
spec:
  host: {{ .Values.global.hostnamePrefix }}-{{ .Values.serviceNameSuffix }}.va-abd-rrd-{{.Values.global.environment}}.svc.cluster.local
  trafficPolicy:
    connectionPool:
      http:
        h2UpgradePolicy: "DO_NOT_UPGRADE"
{{- end }}
