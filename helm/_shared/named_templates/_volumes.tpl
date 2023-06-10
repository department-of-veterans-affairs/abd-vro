
{{/***************************************************************
    Persistent Volumes
*/}}

{{/*
  EBS Volume for Postgres DB
  Containers using this EBS volume must also use pgdata.affinity below

{{- define "vro.volumes.pgdata" -}}
- name: {{ .Values.global.pgdata.pvcName }}
  persistentVolumeClaim:
    claimName: {{ .Values.global.pgdata.pvcName }}
{{- end }}
*/}}

{{- define "vro.volumeMounts.pgdata" -}}
- name: {{ .Values.global.pgdata.pvcName }}
  mountPath: {{ .Values.global.pgdata.mountPath }}
{{- end }}

{{/*
  EFS Volume mount for tracking API requests
*/}}
{{- define "vro.volumes.tracking" -}}
- name: {{ .Values.global.tracking.pvcName }}
  persistentVolumeClaim:
    claimName: {{ .Values.global.tracking.pvcName }}
{{- end }}

{{- define "vro.volumeMounts.tracking" -}}
- name: {{ .Values.global.tracking.pvcName }}
  mountPath: {{ .Values.global.tracking.mountPath }}
{{- end }}

{{/*
  EBS volumes can only be mounted by containers in the same node.
  This affinity added to pod specs ensures the pod runs on the same node.
  https://kubernetes.io/docs/concepts/scheduling-eviction/assign-pod-node/#inter-pod-affinity-and-anti-affinity
  Remember to also add `.Values.global.pgdata.labels` to spec.template.metadata.labels
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

{{/*
   This is needed for example, when the postgres StatefulSet is deleted and redeployed.
   This affinity will cause the StatefulSet to be deployed in the same node as the console pod,
   which has mounted the EBS postgres data volume.
   Without this, the StatefulSet can be created on a different node and will error b/c an EBS
   volume cannot be mounted on multiple nodes simultaneously.
*/}}
{{- define "vro.volume.console.affinity" -}}
affinity:
  podAffinity:
    # Don't use requiredDuringSchedulingIgnoredDuringExecution in case console is not running
    preferredDuringSchedulingIgnoredDuringExecution:
    - weight: 100
      podAffinityTerm:
        labelSelector:
          matchLabels:
            # app label's value must match the labels in postgres/values.yaml
            app: vro-console
        topologyKey: kubernetes.io/hostname
{{- end }}
