ENV=$(tr '[A-Z]' '[a-z]' <<< "$1")
#verify we have an environment set
if [ "${ENV}" != "sandbox" ] && [ "${ENV}" != "qa" ] && [ "${ENV}" != "prod" ]
then
  echo "Please enter valid environment (sandbox, qa, prod)" && exit 1
fi
PREV_ENV=dev
if [ "${ENV}" == "sandbox" ]
then
  PREV_ENV=qa
fi
if [ "${ENV}" == "prod" ]
then
  PREV_ENV=sandbox
fi

if [ "${GITHUB_ACCESS_TOKEN}" == "" ]
then
  echo "please set your github access token environment variable (export GITHUB_ACCESS_TOKEN=XXXXXX)" && exit 2
fi

github_repository=department-of-veterans-affairs/abd-vro
GIT_SHA=$(git rev-parse HEAD)
if [ -n "$2" ]
then
  COMMIT_SHA=$2
  COMMIT_SHA=${COMMIT_SHA:0:7}
else
  COMMIT_SHA=latest
fi

echo PROCESSING FOR $COMMIT_SHA
source scripts/image_vars.src

# Pull previous image (e.g., DEV)
for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
  PREV_NAME=${PREV_ENV}_$(getVarValue ${PREFIX} _IMG)
  echo "Pulling previous image 'PREV_NAME'"
  docker pull "ghcr.io/${github_repository}/${PREV_NAME}:${COMMIT_SHA}"
done

# Promote images for non-prod testing (e.g., DEV to QA)
for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
  PREV_NAME=${PREV_ENV}_$(getVarValue ${PREFIX} _IMG)
  IMAGE_NAME=${ENV}_$(getVarValue ${PREFIX} _IMG)
  echo "Promoting image '$PREV_NAME' to '$IMAGE_NAME'"
  docker tag "ghcr.io/${github_repository}/${PREV_NAME}:${COMMIT_SHA}" "ghcr.io/${github_repository}/${IMAGE_NAME}:${COMMIT_SHA}"
  docker tag "ghcr.io/${github_repository}/${PREV_NAME}:${COMMIT_SHA}" "ghcr.io/${github_repository}/${IMAGE_NAME}:latest"
done

# Push images for non-prod testing (e.g., pushes DEV `latest`)
for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
  IMAGE_NAME=${ENV}_$(getVarValue ${PREFIX} _IMG)
  echo "Pushing image '$IMAGE_NAME'"
  docker push -a "ghcr.io/${github_repository}/${IMAGE_NAME}"
done

# Clean up images locally
for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
  IMAGE_NAME=${ENV}_$(getVarValue ${PREFIX} _IMG)
  echo "Clean up image with tags '$IMAGE_NAME:${COMMIT_SHA:0:7}' and '${IMAGE_NAME}:latest'"
  docker rmi "ghcr.io/${github_repository}/${IMAGE_NAME}:${COMMIT_SHA:0:7}" \
             "ghcr.io/${github_repository}/${IMAGE_NAME}:latest"
done

./deploy-app.sh ${ENV} ${COMMIT_SHA}