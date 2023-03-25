#!/bin/sh
# Used by other scripts to add secrets to K8s.

# Do not modify indentation
echo "
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: $1
data:
$2
"
