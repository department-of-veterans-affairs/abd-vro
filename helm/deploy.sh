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
: ${TRIGGERING_ACTOR:=$USER}

#echo -e "TARGET_ENV=$TARGET_ENV \t HELM_CHART=HELM_CHART \t IMAGE_TAG=$IMAGE_TAG"
#echo -e "RELEASE_NAME=$RELEASE_NAME \t NAMESPACE=$NAMESPACE \t GITHUB_SHA=$GITHUB_SHA"

if [ "${SHUTDOWN_FIRST}" == "true" ]; then
  helm del "$RELEASE_NAME" --wait -n "$NAMESPACE" || exit 5
fi

HELM_ARGS=""

if [ "${ROLLBACK}" == "true" ] || [ "${TARGET_ENV}" == "prod" ]; then
  HELM_ARGS="$HELM_ARGS --atomic"
fi

if [ "${WAIT_TIMEOUT}" ]; then
  HELM_ARGS="$HELM_ARGS --wait --timeout ${WAIT_TIMEOUT}"
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

helmArgsForSubchart(){
  if [ -z "$2" ] || [ "$2" == "(disable)" ]; then
    echo "--set $1.enabled=false"
  else
    echo "--set $1.enabled=true --set-string $1-chart.imageTag=$2"
  fi
}
platformChartArgs(){
  HELM_ARGS="$HELM_ARGS \
    $(helmArgsForSubchart rabbitmq "$RABBITMQ_VER") \
    $(helmArgsForSubchart redis "$REDIS_VER") \
  "
  echo "Platform HELM_ARGS: $HELM_ARGS"
}

case "$HELM_CHART" in
  platform)
    if [ "${SHUTDOWN_FIRST}" == "true" ]; then
      : echo "Since Helm chart was shut down, don't need to delete other charts."
    fi
    platformChartArgs
    ;;
  api-gateway)
    HELM_ARGS="$HELM_ARGS --set-string imageTag=$apigateway_VER ";;
  vro-app)
    HELM_ARGS="$HELM_ARGS --set-string imageTag=$app_VER \
      --set-string dbInit.imageTag=$dbinit_VER "
    ;;
  svc-bgs-api)
    HELM_ARGS="$HELM_ARGS --set-string imageTag=$svcbgsapi_VER ";;
  svc-lighthouse-api)
    HELM_ARGS="$HELM_ARGS --set-string imageTag=$svclighthouseapi_VER ";;
  svc-bip-api)
    HELM_ARGS="$HELM_ARGS --set-string imageTag=$svcbipapi_VER"
esac

#echo "HELM_ARGS: $HELM_ARGS"
set -x
# Exit with error code when command fails so that GH Action fails
set -e
helm upgrade "$RELEASE_NAME" "helm/$HELM_CHART" -n "${NAMESPACE}" \
  --install --reset-values \
  --set-string "global.imageTag=${IMAGE_TAG}" \
  --set-string "global.commitSha=${GITHUB_SHA}" \
  --set-string "global.triggeringActor=${TRIGGERING_ACTOR}" \
  ${HELM_ARGS}
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
[ "${K8S_INFO}" == "true" ] && k8sInfo
