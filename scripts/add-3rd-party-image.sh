#!/bin/bash

# To work around https://www.docker.com/increase-rate-limits/, this script publishes the specified image to GHCR

if [ -z "$2" ]; then
  echo "Usage: $0 <image> <imageTag> [repo]"
  echo "  repo: default value is abd-vro"
  exit 1
fi

# Image to publish
IMAGE="$1"
IMG_TAG="$2"
REPO=${3:-abd-vro}

# ${{ github.repository }}
: ${REPO_PATH:=department-of-veterans-affairs/$REPO}

# Target image; renamed to avoid uncertainty of image being used
TARGET_IMAGE="vro-$IMAGE"
# Target path where the image will be pushed
# https://github.com/orgs/department-of-veterans-affairs/packages?repo_name=abd-vro
GHCR_PATH="ghcr.io/${REPO_PATH}/$TARGET_IMAGE:$IMG_TAG"

# Exit when any command fails
set -e

# Pull, tag, and push these third-party images
echo "# Pulling $IMAGE:$IMG_TAG"
docker pull "$IMAGE:$IMG_TAG"
echo ""
echo "# Pushing/Publishing image $IMAGE:$IMG_TAG to $GHCR_PATH"
docker tag "$IMAGE:$IMG_TAG" "$GHCR_PATH"
docker push "$GHCR_PATH"

echo ""
echo "# Published $GHCR_PATH"
echo "Check for package at https://github.com/orgs/department-of-veterans-affairs/packages?repo_name=$REPO"
echo ""
echo "TODO: create a Helm configuration for the service -- mimic files in helm-service-* folders."
