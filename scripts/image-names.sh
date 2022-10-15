#!/bin/bash

# This is a quick and dirty script to generate image_vars.src that is compatible with Bash 3.
# This script can be replaced with for example a Gradle task later.

# TODO: we should be more consistent about naming our images. This script will
#       help us migrate to get consistency

gradle_folder() {
  case "$1" in
    pdfgenerator|assessclaim*) echo "./service-python/$1";;
    *) echo "./$1";;
  esac
}

gradle_image_name() {
  case "$1" in
    *) echo "va/abd_vro-$1";;
  esac
}

helm_image_key() {
  case "$1" in
    postgres) echo "db";;
    db-init) echo "dbInit";;
    service-data-access) echo "serviceDataAccess";;      # TODO: rename to svcLighthouseApi
    pdfgenerator) echo "pdfGenerator";;                  # TODO: rename to svcPdfGenerator
    assessclaimdc7101) echo "serviceAssessClaimDC7101";; # TODO: rename to svcAssessorDc7101
    assessclaimdc6602) echo "serviceAssessClaimDC6602";; # TODO: rename to svcAssessorDc6602
    app|*) echo "$1";;
  esac
}

bash_var_prefix() {
  helm_image_key "$@"
}

nonprod_image_name() {
  VROENV=${2:-dev}
  echo "${VROENV}_`secrel_image_name $1`"
}

secrel_image_name() {
  case "$1" in
    pdfgenerator|assessclaim*) echo "vro-service-$1";;
    *) echo "vro-$1";;
  esac
}

secrel_dockerfile() {
  case "$1" in
    *) echo "`gradle_folder $1`/src/docker/Dockerfile";;
  esac
}

secrel_docker_context() {
  case "$1" in
    pdfgenerator|assessclaim*) echo "./service-python/$1/src";;
    app|*) echo "./$1/src/main/resources";;
  esac
}

# These names should match directory names
# TODO: rename service-data-access to service-lighthouse-api
IMAGES=( app postgres db-init service-data-access pdfgenerator assessclaimdc7101 assessclaimdc6602)
echo
echo "=== ${#IMAGES[@]} VRO images"
for INDEX in ${!IMAGES[@]}; do
  echo "[$INDEX]: ${IMAGES[$INDEX]}"
done
echo
echo "=== Verifying folders and files"
{
for IMG in ${IMAGES[@]}; do
  echo "--- $IMG"
  ls `gradle_folder $IMG`/build.gradle
  ls `secrel_dockerfile $IMG`
  echo
done
} >> /dev/null

SRC_FILE=scripts/image_vars.src
echo
echo "=== Overwriting $SRC_FILE"
{
  VAR_PREFIXES=()
  for IMG in ${IMAGES[@]}; do
    VAR_PREFIXES+=(`bash_var_prefix $IMG`)
  done

echo "# This file is autogenerated -- update and re-run $0 instead."
echo "# Usage: source \"$SRC_FILE\""
echo '# These variables are used in GitHub Actions and deploy scripts.

# Array of VRO images'
echo "VRO_IMAGES_ARR=( ${IMAGES[@]} )"
echo '# Usage: for IMG in ${VRO_IMAGES_ARR[@]}; do echo "- $IMG"; done'
echo "export VRO_IMAGES=\"${IMAGES[@]}\""
echo
echo '# Array of variable prefixes'
echo "VAR_PREFIXES_ARR=( ${VAR_PREFIXES[@]} )"
echo "export VAR_PREFIXES=\"${VAR_PREFIXES[@]}\""
echo '
# Helper function
# Usage example to get the variable value for app_GRADLE_IMG: GRADLE_IMG_TAG=`getVarValue app _GRADLE_IMG`
getVarValue(){
  local VARNAME=${1}${2}
  echo "${!VARNAME}"
}

# Note: Bash arrays cannot be exported; use this workaround to
#       set the array variable from the string first:'
echo "# IFS=' ' read -ra VAR_PREFIXES_ARR <<< \$VAR_PREFIXES"
echo '# for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
#   echo "## $PREFIX"
#   VARNAME=${PREFIX}_GRADLE_IMG
#   echo "$VARNAME = `getVarValue ${PREFIX} _GRADLE_IMG`"
#   echo
# done

'
echo
  for IMG in ${IMAGES[@]}; do
    echo "# --- $IMG in folder `gradle_folder $IMG`"
    PREFIX=`bash_var_prefix $IMG`
    echo "export ${PREFIX}_GRADLE_IMG=\"`gradle_image_name $IMG`\""
    echo "export ${PREFIX}_IMG=\"`secrel_image_name $IMG`\""

    echo "export ${PREFIX}_DOCKERFILE=\"`secrel_dockerfile $IMG`\""
    echo "export ${PREFIX}_DOCKER_CONTEXT=\"`secrel_docker_context $IMG`\""

    echo "export ${PREFIX}_HELM_KEY=\"`helm_image_key $IMG`\""
    echo
  done
} > "$SRC_FILE"

images_for_secrel_config_yml(){
  echo
  echo "=== The following can be pasted into .github/secrel/config.yml"
for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
  echo "- name: `getVarValue ${PREFIX} _IMG`
  context: \"`getVarValue ${PREFIX} _DOCKER_CONTEXT`\"
  path: \"`getVarValue ${PREFIX} _DOCKERFILE`\""
done
}

images_for_helmchart_values_yaml(){
  local _ENV=$1
  echo
  echo "=== The following can be pasted into helmchart/values.yaml"
for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
  echo "  `getVarValue ${PREFIX} _HELM_KEY`:
    imageName: ${_ENV}_`getVarValue ${PREFIX} _IMG`
    tag: 0b9c9c4
    imagePullPolicy: Always"
done
}

source "$SRC_FILE"
images_for_secrel_config_yml
images_for_helmchart_values_yaml dev