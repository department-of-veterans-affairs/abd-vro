{{/***************************************************************
    Persistent Volumes
*/}}

{{/*
  EBS Volume for Postgres DB
  Containers using this EBS volume must also use pgdata.affinity below

{{- define "vro.volumes.pgdata" -}}
- name: {{ .Values.global.pv.pgdata.pvcName }}
  persistentVolumeClaim:
    claimName: {{ .Values.global.pv.pgdata.pvcName }}
{{- end }}
*/}}

{{- define "vro.volumeMounts.pgdata" -}}
- name: {{ .Values.global.pv.pgdata.pvcName }}
  mountPath: {{ .Values.global.pv.pgdata.mountPath }}
{{- end }}

{{/*
  EFS Volume mount for tracking API requests
*/}}
{{- define "vro.volumes.tracking" -}}
- name: {{ .Values.global.pv.tracking.pvcName }}
  persistentVolumeClaim:
    claimName: {{ .Values.global.pv.tracking.pvcName }}
{{- end }}

{{- define "vro.volumeMounts.tracking" -}}
- name: {{ .Values.global.pv.tracking.pvcName }}
  mountPath: {{ .Values.global.pv.tracking.mountPath }}
{{- end }}

{{/*
  EBS volumes can only be mounted by containers in the same node.
  This affinity added to pod specs ensures the pod runs on the same node.
  https://kubernetes.io/docs/concepts/scheduling-eviction/assign-pod-node/#inter-pod-affinity-and-anti-affinity
  Remember to also add `.Values.global.pv.pgdata.labels` to spec.template.metadata.labels
*/}}
{{- define "vro.volume.pgdata.affinity" -}}
affinity:
  podAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
    - labelSelector:
        matchLabels:
          # app label's value must match the labels in postgres/values.yaml
          app: vro-postgres
      # https://stackoverflow.com/questions/72240224/what-is-topologykey-in-pod-affinity
      # https://stackoverflow.com/a/68276317
      topologyKey: kubernetes.io/hostname
{{- end }}
