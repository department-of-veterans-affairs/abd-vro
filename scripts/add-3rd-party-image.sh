#!/bin/bash
# To work around https://www.docker.com/increase-rate-limits/, this script publishes the specified image to GHCR

# Image to publish
IMAGE="$1"
IMG_TAG="$2"
REPO=${3:-abd-vro-internal}

if [ -z "$IMG_TAG" ]; then
  echo "Usage: $0 <image> <imageTag> [repo]"
  echo "  repo: default value is $REPO"
  exit 1
fi

# ${{ github.repository }}
: ${REPO_PATH:=department-of-veterans-affairs/$REPO}

# Target image; renamed to avoid uncertainty of image being used
TARGET_IMAGE="vro-$IMAGE"
# Target path where the image will be pushed
GHCR_PATH="ghcr.io/${REPO_PATH}/$TARGET_IMAGE:$IMG_TAG"

# Exit when any command fails
set -e

# Pull, tag, and push these third-party images
echo "
# Pulling $IMAGE:$IMG_TAG"
docker pull "$IMAGE:$IMG_TAG"

echo "
# Pushing/Publishing image $IMAGE:$IMG_TAG to $GHCR_PATH and tagged as 'latest'"
docker tag "$IMAGE:$IMG_TAG" "$GHCR_PATH"
docker push "$GHCR_PATH"

docker tag "$IMAGE:$IMG_TAG" "ghcr.io/${REPO_PATH}/$TARGET_IMAGE:latest"
docker push "ghcr.io/${REPO_PATH}/$TARGET_IMAGE:latest"

echo "
# Published $GHCR_PATH
Check for package at https://github.com/orgs/department-of-veterans-affairs/packages?repo_name=$REPO
"
echo "
Next, you must do the following:
1. Manually connect the package to the $REPO repo and
   set the package to "Inherit access from source repository" as instructed by LHDI doc:
   https://animated-carnival-57b3e7f5.pages.github.io/starterkits/java/development-guide/#changing-published-package-visibility
   Also see https://github.com/department-of-veterans-affairs/abd-vro/wiki/Docker-containers#packages
   The package should now be found at https://github.com/department-of-veterans-affairs/$REPO/pkgs/container/$REPO%2Fvro-$IMAGE

2. Create a Helm configuration for the service -- mimic files in helm-service-* folders.

3. Create and test scripts/deploy-$IMAGE.sh
"
