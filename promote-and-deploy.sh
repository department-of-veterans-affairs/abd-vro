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
docker pull "ghcr.io/${github_repository}/${PREV_ENV}_vro-app:${COMMIT_SHA}"
docker pull "ghcr.io/${github_repository}/${PREV_ENV}_vro-postgres:${COMMIT_SHA}"
docker pull "ghcr.io/${github_repository}/${PREV_ENV}_vro-db-init:${COMMIT_SHA}"
docker pull "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-assessclaimdc7101:${COMMIT_SHA}"
docker pull "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-assessclaimdc6602:${COMMIT_SHA}"
docker pull "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-pdfgenerator:${COMMIT_SHA}"
docker pull "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-data-access:${COMMIT_SHA}"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-app:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-app:${COMMIT_SHA}"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-postgres:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-postgres:${COMMIT_SHA}"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-db-init:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-db-init:${COMMIT_SHA}"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-assessclaimdc7101:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc7101:${COMMIT_SHA}"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-assessclaimdc6602:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc6602:${COMMIT_SHA}"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-pdfgenerator:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-service-pdfgenerator:${COMMIT_SHA}"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-data-access:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-service-data-access:${COMMIT_SHA}"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-app:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-app:latest"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-postgres:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-postgres:latest"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-db-init:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-db-init:latest"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-assessclaimdc7101:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc7101:latest"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-assessclaimdc6602:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc6602:latest"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-pdfgenerator:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-service-pdfgenerator:latest"
docker tag "ghcr.io/${github_repository}/${PREV_ENV}_vro-service-data-access:${COMMIT_SHA}" "ghcr.io/${github_repository}/${ENV}_vro-service-data-access:latest"
docker push -a ghcr.io/${github_repository}/${ENV}_vro-postgres
docker push -a ghcr.io/${github_repository}/${ENV}_vro-app
docker push -a ghcr.io/${github_repository}/${ENV}_vro-service-pdfgenerator
docker push -a ghcr.io/${github_repository}/${ENV}_vro-db-init
docker push -a ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc7101
docker push -a ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc6602
docker push -a ghcr.io/${github_repository}/${ENV}_vro-service-data-access
docker rmi ghcr.io/${github_repository}/${ENV}_vro-postgres:${COMMIT_SHA} \
            ghcr.io/${github_repository}/${ENV}_vro-app:${COMMIT_SHA} \
            ghcr.io/${github_repository}/${ENV}_vro-service-pdfgenerator:${COMMIT_SHA} \
            ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc7101:${COMMIT_SHA} \
            ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc6602:${COMMIT_SHA} \
            ghcr.io/${github_repository}/${ENV}_vro-db-init:${COMMIT_SHA} \
            ghcr.io/${github_repository}/${ENV}_vro-service-data-access:${COMMIT_SHA} \
            ghcr.io/${github_repository}/${ENV}_vro-postgres:latest \
            ghcr.io/${github_repository}/${ENV}_vro-app:latest \
            ghcr.io/${github_repository}/${ENV}_vro-db-init:latest \
            ghcr.io/${github_repository}/${ENV}_vro-service-pdfgenerator:latest \
            ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc7101:latest \
            ghcr.io/${github_repository}/${ENV}_vro-service-assessclaimdc6602:latest \
            ghcr.io/${github_repository}/${ENV}_vro-service-data-access:latest 
./deploy-app.sh ${ENV} ${COMMIT_SHA}