#!/bin/bash

if [ -z "$2" ]; then
  echo "Usage: $0 TARGET_ENV HELM_CHART [IMAGE_TAG]"
  exit 10
fi

TARGET_ENV=$1
case "$TARGET_ENV" in
  dev|qa|sandbox|prod-test|prod) ;;
  *) echo "ERROR: Unknown LHDI environment: $TARGET_ENV"; exit 21;;
esac

HELM_CHART=$2
if ! [ -d "helm/$HELM_CHART" ]; then
  echo "ERROR: Unknown Helm chart: $HELM_CHART"
  exit 22
fi

: ${IMAGE_TAG:="${3:-latest}"}
RELEASE_NAME="vro-$HELM_CHART"
NAMESPACE=va-abd-rrd-${TARGET_ENV}
: ${GITHUB_SHA:=$(git rev-parse HEAD)}

#echo -e "TARGET_ENV=$TARGET_ENV \t HELM_CHART=HELM_CHART \t IMAGE_TAG=$IMAGE_TAG"
#echo -e "RELEASE_NAME=$RELEASE_NAME \t NAMESPACE=$NAMESPACE \t GITHUB_SHA=$GITHUB_SHA"

if [ "${SHUTDOWN_FIRST}" ]; then
  helm del "$RELEASE_NAME" --wait -n "$NAMESPACE" || exit 5
fi

HELM_ARGS=""

if [ "${ROLLBACK}" == "true" ] || [ "${TARGET_ENV}" == "prod" ]; then
  HELM_ARGS="$HELM_ARGS --atomic"
fi

if [ "${WAIT_TIMEOUT}" ]; then
  HELM_ARGS="$HELM_ARGS --wait --timeout '${WAIT_TIMEOUT}'"
fi

# Load values from files first; command-line parameters can override these values
# Order of these files matter; contents of latter files will override earlier files
for VALUES_FILE in \
  "helm/_shared/values.yaml" \
  "helm/_shared/values-for-${TARGET_ENV}.yaml" \
  "helm/$HELM_CHART/values.yaml" \
  "helm/$HELM_CHART/values-for-${TARGET_ENV}.yaml"
do
  if [ -e "${VALUES_FILE}" ]; then
    HELM_ARGS="$HELM_ARGS -f $VALUES_FILE"
  fi
done

# Workaround for postgres StatefulSet
# If upgrading existing deployment and you want postgres and it's already enabled, then
# apply the workaround to delete StatefulSets without deleting pods (allow pods to be temporarily orphaned)
deletePostgresStatefulSet(){
  echo "Deleting StatefulSet for Postgres"
  # This is a workaround for `helm upgrade` reporting error due to a StatefulSet:
  #   Error: UPGRADE FAILED: cannot patch "vro-postgres" with kind StatefulSet:
  #     Forbidden: updates to statefulset spec for fields other than ...
  # See https://github.com/helm/helm/issues/2149#issuecomment-341701817
  kubectl delete statefulsets vro-postgres -n "$NAMESPACE" \
    --cascade=orphan --wait --ignore-not-found=true
}

platformChartArgs(){
  helmArgsForSubchart(){
    if [ -z "$2" ] || [ "$2" == "(disable)" ]; then
      echo "--set $1.enabled=false"
    else
      echo "--set $1.enabled=true --set-string $1-chart.imageTag=$2"
    fi
  }
  HELM_ARGS="$HELM_ARGS \
    $(helmArgsForSubchart rabbitmq "$RABBITMQ_VER") \
    $(helmArgsForSubchart redis "$REDIS_VER") \
    --set postgres.enabled=$ENABLE_POSTGRES \
    --set console.enabled=$ENABLE_CONSOLE \
  "
  echo "Platform HELM_ARGS: $HELM_ARGS"
}

case "$HELM_CHART" in
  platform)
    : ${ENABLE_POSTGRES:=true}
    : ${ENABLE_CONSOLE:=false}
    : ${RABBITMQ_VER:=3}
    : ${REDIS_VER:=7}
    if [ "${SHUTDOWN_FIRST}" ]; then
      : echo "Since Helm chart was shut down, don't need to delete other charts."
    elif [ "$ENABLE_POSTGRES" == "true" ]; then
      deletePostgresStatefulSet
    fi
    platformChartArgs
    ;;
esac

#echo "HELM_ARGS: $HELM_ARGS"
set -x
helm upgrade "$RELEASE_NAME" "helm/$HELM_CHART" -n "${NAMESPACE}" \
  --install --reset-values \
  ${HELM_ARGS} \
  --set-string "global.imageTag=${IMAGE_TAG}" \
  --set-string "global.commitSha=${GITHUB_SHA}"
set +x

k8sInfo(){
  echo "==================================="
  helm get values "${RELEASE_NAME}" -n "${NAMESPACE}"
  kubectl -n "${NAMESPACE}" get pods --show-labels
  # https://kubernetes.io/docs/reference/kubectl/jsonpath/
  kubectl -n "${NAMESPACE}" get pods -o jsonpath='{range .items[*]}
    {"pod: "}{.metadata.name}{range .spec.containers[*]}
    {"\tname: "}{.name}
    {"\timage: "}{.image}{end}'
  kubectl -n "${NAMESPACE}" get pvc
  kubectl -n "${NAMESPACE}" get services
  kubectl -n "${NAMESPACE}" get events
}
[ "$K8SINFO" == "true" ] && k8sInfo
