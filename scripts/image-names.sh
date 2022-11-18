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
    svc-lighthouse-api) echo "va/abd_vro-service-data-access";; # TODO: update image name
    *) echo "va/abd_vro-$1";;
  esac
}

helm_image_key() {
  case "$1" in
    postgres) echo "db";;
    db-init) echo "dbInit";;
    svc-lighthouse-api) echo "serviceDataAccess";;       # TODO: rename to svcLighthouseApi
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
  echo "${VROENV}_$(secrel_image_name "$1")"
}

secrel_image_name() {
  case "$1" in
    svc-lighthouse-api) echo "vro-service-data-access";; # TODO: update image name
    pdfgenerator|assessclaim*) echo "vro-service-$1";;
    *) echo "vro-$1";;
  esac
}

secrel_dockerfile() {
  GRADLE_FOLDER=$(gradle_folder "$1")
  case "$1" in
    pdfgenerator|assessclaim*) echo "./service-python/Dockerfile";;
    *) echo "$GRADLE_FOLDER/src/docker/Dockerfile";;
  esac
}

secrel_docker_context() {
  GRADLE_FOLDER=$(gradle_folder "$1")
  case "$1" in
    pdfgenerator|assessclaim*) echo "./service-python";;
    *) echo "$GRADLE_FOLDER/build/docker";;
  esac
}

secrel_docker_build_args() {
  GRADLE_FOLDER=$(gradle_folder "$1")
  case "$1" in
    pdfgenerator|assessclaim*) echo "    - SERVICE_SRC_FOLDER=$1/build/docker";;
    app|console|svc-lighthouse-api) echo "    - JAR_FILE=$1-*.jar
    - ENTRYPOINT_FILE=entrypoint.sh";;
    *) echo "";;
  esac
}

# These names should match directory names
IMAGES=( app postgres db-init console svc-lighthouse-api pdfgenerator assessclaimdc7101 assessclaimdc6602 )
echo
echo "=== ${#IMAGES[@]} VRO images"
for INDEX in "${!IMAGES[@]}"; do
  echo "[$INDEX]: ${IMAGES[$INDEX]}"
done
echo
echo "=== Verifying folders and files"
{
for IMG in "${IMAGES[@]}"; do
  echo "--- $IMG"
  GRADLE_FOLDER=$(gradle_folder "$IMG")
  ls "$GRADLE_FOLDER/build.gradle"
  ls "$(secrel_dockerfile "$IMG")"
  echo
done
} >> /dev/null

SRC_FILE=scripts/image_vars.src
echo
echo "=== Overwriting $SRC_FILE"
# shellcheck disable=SC2145,SC2016
overwriteSrcFile(){
  VAR_PREFIXES=()
  for IMG in "${IMAGES[@]}"; do
    VAR_PREFIXES+=( "$(bash_var_prefix "$IMG")" )
  done

echo '#!/bin/bash'
echo "# This file is autogenerated -- update and re-run $0 instead."
echo "# Usage: source \"$SRC_FILE\""
echo '# These variables are used in GitHub Actions and deploy scripts.

# Array of VRO images
# shellcheck disable=SC2034'
echo "VRO_IMAGES_ARR=( ${IMAGES[@]} )"
echo '# Usage: for IMG in ${VRO_IMAGES_ARR[@]}; do echo "- $IMG"; done'
echo "export VRO_IMAGES=\"${IMAGES[@]}\""
echo
echo '# Array of variable prefixes
# shellcheck disable=SC2034'
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
  for IMG in "${IMAGES[@]}"; do
    GRADLE_FOLDER=$(gradle_folder "$IMG")
    echo "# --- $IMG in folder $GRADLE_FOLDER"
    PREFIX=$(bash_var_prefix "$IMG")
    echo "export ${PREFIX}_GRADLE_IMG=\"$(gradle_image_name "$IMG")\""
    echo "export ${PREFIX}_IMG=\"$(secrel_image_name "$IMG")\""

    echo "export ${PREFIX}_DOCKERFILE=\"$(secrel_dockerfile "$IMG")\""
    echo "export ${PREFIX}_DOCKER_CONTEXT=\"$(secrel_docker_context "$IMG")\""

    echo "export ${PREFIX}_HELM_KEY=\"$(helm_image_key "$IMG")\""
    echo
  done
  echo '# End of file'
}
overwriteSrcFile > "$SRC_FILE"

images_for_secrel_config_yml(){
  echo '# BEGIN image-names.sh replacement block (do not modify this line)
# The following image list is updated by image-names.sh'
for IMG in "${IMAGES[@]}"; do
  echo "- name: $(secrel_image_name "$IMG")
  context: \"$(secrel_docker_context "$IMG")\"
  path: \"$(secrel_dockerfile "$IMG")\""
  BUILD_ARGS="$(secrel_docker_build_args "$IMG")"
  if [ "$BUILD_ARGS" ]; then
    echo "  args:
$BUILD_ARGS"
  fi
done
echo '# END image-names.sh replacement block (do not modify this line)'
}

images_for_helmchart_values_yaml(){
  local _ENV=$1
  echo '# BEGIN image-names.sh replacement block (do not modify this line)
# The following image list is updated by image-names.sh'
for PREFIX in "${VAR_PREFIXES_ARR[@]}"; do
  echo "  $(getVarValue "${PREFIX}" _HELM_KEY):
    imageName: ${_ENV}_$(getVarValue "${PREFIX}" _IMG)
    tag: tagPlaceholder
    imagePullPolicy: Always"
done
echo '# END image-names.sh replacement block (do not modify this line)'
}

# shellcheck source=image_vars.src
source "$SRC_FILE"
SEC_CONFIG_IMAGES=$(images_for_secrel_config_yml)
VALUES_YML_IMAGES=$(images_for_helmchart_values_yaml dev)

if which sed > /dev/null; then
  echo "=== Writing images to .github/secrel/config-updated.yml"
  sed -e '/^# BEGIN image-names.sh/,/^# END image-names.sh/{ r /dev/stdin' -e ';d;}' \
    .github/secrel/config.yml <<< "$SEC_CONFIG_IMAGES" > .github/secrel/config-updated.yml
  echo "Differences:"
  diff .github/secrel/config.yml .github/secrel/config-updated.yml

  echo "=== Writing images to helmchart/values-updated.yaml"
  sed -e '/^# BEGIN image-names.sh/,/^# END image-names.sh/{ r /dev/stdin' -e ';d;}' \
    helmchart/values.yaml <<< "$VALUES_YML_IMAGES" > helmchart/values-updated.yaml
  echo "Differences:"
  diff helmchart/values.yaml helmchart/values-updated.yaml
else
  echo
  echo "=== Paste the following into .github/secrel/config.yml"
  echo "$SEC_CONFIG_IMAGES"
  echo
  echo "=== Paste the following into helmchart/values.yaml"
  echo "$VALUES_YML_IMAGES"
fi
