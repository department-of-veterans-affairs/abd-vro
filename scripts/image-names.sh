#!/bin/bash

# This is a quick and dirty script to generate image_vars.src that is compatible with Bash 3.
# This script can be replaced with for example a Gradle task later.

# Be consistent about naming our images. This script helps us understand inconsistencies.

gradle_folder() {
  case "$1" in
    pdfgenerator|featuretoggle|assessclaim*) echo "./domain-rrd/service-python/$1";;
    cc-app) echo "domain-cc/$1";;
    *) echo "./$1";;
  esac
}

# These are used in docker-compose.yml files
gradle_image_name() {
  echo "va/abd_vro-$1"
}

# Bash variables can't have dashes, so strip them out of the directory names
bash_var_prefix() {
  echo "${1//-/}"
}

# These names must match the images specified in Helm configs
prod_image_name() {
  echo "vro-$1"
}

# These names should match directory names, if the docker image is built in a
# subdirectory, be sure to add the sub directory to the gradle_folder function above
IMAGES=( api-gateway app postgres db-init console svc-bgs-api svc-lighthouse-api cc-app )
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
## Helper functions
# Usage example to get the variable value for app_GRADLE_IMG: GRADLE_IMG_TAG=`getVarValue app _GRADLE_IMG`
getVarValue(){
  local VARNAME=${1}${2}
  echo "${!VARNAME}"
}

# Return non-zero error code if image tag does not exist
# Usage: imageTagExists IMAGE_NAME IMAGE_TAG
# Environment variable GHCR_TOKEN should be in base64
imageTagExists(){
  [ "$GHCR_TOKEN" ] || { echo "GHCR_TOKEN not set!" >&2; return 2; }
  # https://superuser.com/a/442395
  curl -s -o /dev/null -w "%{http_code}" -I -H "Authorization: Bearer ${GHCR_TOKEN}" \
    "https://ghcr.io/v2/department-of-veterans-affairs/abd-vro-internal/$1/manifests/$2"
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
  echo '######################################'
  echo
  for IMG in "${IMAGES[@]}"; do
    GRADLE_FOLDER=$(gradle_folder "$IMG")
    echo "# --- $IMG in folder $GRADLE_FOLDER"
    PREFIX=$(bash_var_prefix "$IMG")
    echo "export ${PREFIX}_GRADLE_IMG=\"$(gradle_image_name "$IMG")\""
    echo "export ${PREFIX}_IMG=\"$(prod_image_name "$IMG")\""
    echo
  done

  echo '######################################'
  echo 'source scripts/image_versions.src'
  echo '# End of file'
}
overwriteSrcFile > "$SRC_FILE"
