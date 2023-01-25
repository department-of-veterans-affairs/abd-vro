#!/bin/bash

# For debugging, print out each command
set -x

# Fail fast when any command fails, i.e., pushing image to ghcr
set -e

# ${{ github.repository }}
REPO="$1"
# A tag label for all the images, e.g., ${COMMIT_SHA:0:7}
# Images will also be tagged as 'latest', overriding any previous images
IMG_TAG="$2"
# Target environment, e.g., 'dev' or 'qa'
# Used as a prefix for the image name
TARGET_ENV="$3"

# Tag and push images using commit hash and `latest`
source scripts/image_vars.src
for PREFIX in "${VAR_PREFIXES_ARR[@]}"; do
  GRADLE_IMG_NAME=$(getVarValue "${PREFIX}" _GRADLE_IMG)
  IMG_NAME=${TARGET_ENV}_$(getVarValue "${PREFIX}" _IMG)

  echo "Tagging image '$GRADLE_IMG_NAME' as '$IMG_NAME:${IMG_TAG}' and 'latest'"
  docker tag "$GRADLE_IMG_NAME" "ghcr.io/${REPO}/${IMG_NAME}:${IMG_TAG}"
  docker tag "$GRADLE_IMG_NAME" "ghcr.io/${REPO}/${IMG_NAME}:latest"

  echo "Pushing all tags for image '$IMG_NAME'"
  docker push --all-tags "ghcr.io/${REPO}/${IMG_NAME}"
done
