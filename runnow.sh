#!/usr/bin/env bash

# Pull and tag these images to work around https://www.docker.com/increase-rate-limits/
docker pull rabbitmq:3
docker tag rabbitmq:3 "ghcr.io/department-of-veterans-affairs/abd-vro/vro-rabbitmq:3"

source scripts/image_vars.src

# Tag dev images for non-prod testing
for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
GRADLE_IMG_NAME=$(getVarValue ${PREFIX} _GRADLE_IMG)
IMG_NAME=dev_$(getVarValue ${PREFIX} _IMG)
echo "Tagging image '$GRADLE_IMG_NAME' as '$IMG_NAME'"

docker tag "$GRADLE_IMG_NAME" "ghcr.io/department-of-veterans-affairs/abd-vro/${IMG_NAME}:aabd181"
docker tag "$GRADLE_IMG_NAME" "ghcr.io/department-of-veterans-affairs/abd-vro/${IMG_NAME}:latest"
done
#
#
## Reminder to also add new images to build.yml and .github/secrel/config.yml
#
## Push the following images to work around https://www.docker.com/increase-rate-limits/
docker push "ghcr.io/department-of-veterans-affairs/abd-vro/vro-rabbitmq:3"
#
##source scripts/image_vars.src
#
## Push dev images for non-prod testing
for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
IMG_NAME=dev_$(getVarValue ${PREFIX} _IMG)
echo "Pushing image '$IMG_NAME'"
docker push -a "ghcr.io/department-of-veterans-affairs/abd-vro/${IMG_NAME}"
echo "-------------------------------------------"
done

#
##source scripts/image_vars.src
#
## Clean up dev images locally
#for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
#IMG_NAME=dev_$(getVarValue ${PREFIX} _IMG)
#echo "Clean up image with tags '$IMG_NAME:aabd181' and '${IMG_NAME}:latest'"
#docker rmi "ghcr.io/department-of-veterans-affairs/abd-vro/${IMG_NAME}:aabd181" \
#            "ghcr.io/department-of-veterans-affairs/abd-vro/${IMG_NAME}:latest"
#done
#
#./scripts/deploy-app.sh dev aabd181 #${COMMIT_SHA:0:7}
