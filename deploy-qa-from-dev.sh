if [ "${GITHUB_ACCESS_TOKEN}" == "" ]
then
  echo "please set your github access token environment variable (export GITHUB_ACCESS_TOKEN=XXXXXX)" && exit 2
fi
github_repository=department-of-veterans-affairs/abd-vro
GIT_SHA=$(git rev-parse HEAD)
if [ -n "$1" ]
then
  COMMIT_SHA=$1
else
  COMMIT_SHA=${GIT_SHA:0:7}
fi
docker pull "ghcr.io/${github_repository}/dev_vro-app:${COMMIT_SHA:0:7}"
docker pull "ghcr.io/${github_repository}/dev_vro-postgres:${COMMIT_SHA:0:7}"
docker pull "ghcr.io/${github_repository}/dev_vro-init-db:${COMMIT_SHA:0:7}"
docker pull "ghcr.io/${github_repository}/dev_vro-service-assessclaimdc7101:${COMMIT_SHA:0:7}"
docker pull "ghcr.io/${github_repository}/dev_vro-service-assessclaimdc6602:${COMMIT_SHA:0:7}"
docker pull "ghcr.io/${github_repository}/dev_vro-service-pdfgenerator:${COMMIT_SHA:0:7}"
docker pull "ghcr.io/${github_repository}/dev_vro-service-data-access:${COMMIT_SHA:0:7}"
docker tag "ghcr.io/${github_repository}/dev_vro-app:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-app:${COMMIT_SHA:0:7}"
docker tag "ghcr.io/${github_repository}/dev_vro-postgres:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-postgres:${COMMIT_SHA:0:7}"
docker tag "ghcr.io/${github_repository}/dev_vro-init-db:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-init-db:${COMMIT_SHA:0:7}"
docker tag "ghcr.io/${github_repository}/dev_vro-service-assessclaimdc7101:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-service-assessclaimdc7101:${COMMIT_SHA:0:7}"
docker tag "ghcr.io/${github_repository}/dev_vro-service-assessclaimdc6602:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-service-assessclaimdc6602:${COMMIT_SHA:0:7}"
docker tag "ghcr.io/${github_repository}/dev_vro-service-pdfgenerator:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-service-pdfgenerator:${COMMIT_SHA:0:7}"
docker tag "ghcr.io/${github_repository}/dev_vro-service-data-access:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-service-data-access:${COMMIT_SHA:0:7}"
docker tag "ghcr.io/${github_repository}/dev_vro-app:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-app:latest"
docker tag "ghcr.io/${github_repository}/dev_vro-postgres:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-postgres:latest"
docker tag "ghcr.io/${github_repository}/dev_vro-init-db:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-init-db:latest"
docker tag "ghcr.io/${github_repository}/dev_vro-service-assessclaimdc7101:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-service-assessclaimdc7101:latest"
docker tag "ghcr.io/${github_repository}/dev_vro-service-assessclaimdc6602:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-service-assessclaimdc6602:latest"
docker tag "ghcr.io/${github_repository}/dev_vro-service-pdfgenerator:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-service-pdfgenerator:latest"
docker tag "ghcr.io/${github_repository}/dev_vro-service-data-access:${COMMIT_SHA:0:7}" "ghcr.io/${github_repository}/qa_vro-service-data-access:latest"
docker push -a ghcr.io/${github_repository}/qa_vro-postgres
docker push -a ghcr.io/${github_repository}/qa_vro-app
docker push -a ghcr.io/${github_repository}/qa_vro-service-pdfgenerator
docker push -a ghcr.io/${github_repository}/qa_vro-init-db
docker push -a ghcr.io/${github_repository}/qa_vro-service-assessclaimdc7101
docker push -a ghcr.io/${github_repository}/qa_vro-service-assessclaimdc6602
docker push -a ghcr.io/${github_repository}/qa_vro-service-data-access
docker rmi ghcr.io/${github_repository}/qa_vro-postgres:${COMMIT_SHA:0:7} \
            ghcr.io/${github_repository}/qa_vro-app:${COMMIT_SHA:0:7} \
            ghcr.io/${github_repository}/qa_vro-service-pdfgenerator:${COMMIT_SHA:0:7} \
            ghcr.io/${github_repository}/qa_vro-service-assessclaimdc7101:${COMMIT_SHA:0:7} \
            ghcr.io/${github_repository}/qa_vro-service-assessclaimdc6602:${COMMIT_SHA:0:7} \
            ghcr.io/${github_repository}/qa_vro-init-db:${COMMIT_SHA:0:7} \
            ghcr.io/${github_repository}/qa_vro-service-data-access:${COMMIT_SHA:0:7} \
            ghcr.io/${github_repository}/qa_vro-postgres:latest \
            ghcr.io/${github_repository}/qa_vro-app:latest \
            ghcr.io/${github_repository}/qa_vro-init-db:latest \
            ghcr.io/${github_repository}/qa_vro-service-pdfgenerator:latest \
            ghcr.io/${github_repository}/qa_vro-service-assessclaimdc7101:latest \
            ghcr.io/${github_repository}/qa_vro-service-assessclaimdc6602:latest \
            ghcr.io/${github_repository}/qa_vro-service-data-access:latest 
./deploy-app.sh qa ${COMMIT_SHA:0:7}