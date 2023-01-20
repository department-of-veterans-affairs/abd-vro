#!/bin/bash

# To work around https://www.docker.com/increase-rate-limits/, this script publishes the specified image to GHCR

# ${{ github.repository }}
: ${REPO:=department-of-veterans-affairs/abd-vro}

# Image to publish
IMAGE="rabbitmq"
IMG_TAG="3"

# Target image; renamed to avoid uncertainty of image being used
TARGET_IMAGE="vro-$IMAGE"
# Target path where the image will be pushed
# https://github.com/orgs/department-of-veterans-affairs/packages?repo_name=abd-vro
GHCR_PATH="ghcr.io/${REPO}/$TARGET_IMAGE:$IMG_TAG"

# Pull, tag, and push these third-party images
docker pull "$IMAGE:$IMG_TAG"
docker tag "$IMAGE:$IMG_TAG" "$GHCR_PATH"
docker push "$GHCR_PATH"

echo "Remember to create a helm configuration for the service -- mimic helm-service-* folders."
