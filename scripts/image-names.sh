#!/bin/bash

# This is a quick and dirty script to generate image_vars.src that is compatible with Bash 3.
# This script can be replaced with for example a Gradle task later.

# Be consistent about naming our images. This script helps us understand inconsistencies.

# These are used in docker-compose.yml files
gradleImageName() {
  echo "va/abd_vro-$1"
}

# Bash variables cannot have dashes, so strip them out of the directory names
bashVarPrefix() {
  echo "${1//-/}"
}

# These names must match the images specified in Helm configs
prodImageName() {
  echo "vro-$1"
}

# These names should match directory names.
# Also add image to .github/workflows/delete-published-*images.yml
IMAGES=( postgres \
  redis \
  rabbitmq \
  api-gateway app db-init \
  dev-tools \
  svc-bgs-api \
  svc-lighthouse-api svc-bie-kafka svc-bip-api \
  xample-workflows \
  cc-app \
  ee-max-cfi-app \
  ee-ep-merge-app )
echo
echo "=== ${#IMAGES[@]} VRO images"
for INDEX in "${!IMAGES[@]}"; do
  echo "[$INDEX]: ${IMAGES[$INDEX]}"
done

SRC_FILE=scripts/image_vars.src
echo
echo "=== Overwriting $SRC_FILE"
# shellcheck disable=SC2145,SC2016
overwriteSrcFile(){
  VAR_PREFIXES=()
  for IMG in "${IMAGES[@]}"; do
    VAR_PREFIXES+=( "$(bashVarPrefix "$IMG")" )
  done

  echo '#!/bin/bash'
  echo "# This file is autogenerated -- update and re-run $0 instead."
  echo "# Usage: source $SRC_FILE"
  echo '# These variables are used in GitHub Actions and deploy scripts.'
  echo
  echo '# Array of VRO images'
  echo '# shellcheck disable=SC2034'
  echo "VRO_IMAGES_ARR=( ${IMAGES[@]} )"
  echo '# Usage: for IMG in ${VRO_IMAGES_ARR[@]}; do echo "- $IMG"; done'
  echo "export VRO_IMAGES=\"${IMAGES[@]}\""
  echo
  echo '# Array of variable prefixes'
  echo '# shellcheck disable=SC2034'
  echo "VAR_PREFIXES_ARR=( ${VAR_PREFIXES[@]} )"
  echo "export VAR_PREFIXES=\"${VAR_PREFIXES[@]}\""
  echo
  echo 'LAST_RELEASE_VERSION=$(tail -1 versions.txt)'
  echo '
## Helper functions
# Usage example to get the variable value for app_GRADLE_IMG: GRADLE_IMG_TAG=`getVarValue app _GRADLE_IMG`
getVarValue(){
  local VARNAME=${1}${2}
  echo "${!VARNAME}"
}

# Bash variables cannot have dashes, so strip them out of the directory names
bashVarPrefix() {
  echo "${1//-/}"
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
#       set the array variable from the string first if needed:'
  echo "# IFS=' ' read -ra VAR_PREFIXES_ARR <<< \$VAR_PREFIXES"
  echo '#       Then use it like normal:
# for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
#   echo "The value of ${PREFIX}_GRADLE_IMG is $(getVarValue ${PREFIX} _GRADLE_IMG)"
#   echo
# done

imageVersions(){
 # shellcheck disable=SC2068
 for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
   echo "$(getVarValue ${PREFIX} _IMG) $(getVarValue ${PREFIX} _VER)"
 done
}

######################################
'

  # Load current image versions by setting *_VER variables
  >&2 source scripts/image_versions.src
  getVarValue(){
    local VARNAME=${1}${2}
    echo "${!VARNAME}"
  }
  for IMG in "${IMAGES[@]}"; do
    local PREFIX=$(bashVarPrefix "$IMG")
    echo "export ${PREFIX}_GRADLE_IMG=\"$(gradleImageName "$IMG")\""
    echo "export ${PREFIX}_IMG=\"$(prodImageName "$IMG")\""
    echo "export ${PREFIX}_VER=\"\$LAST_RELEASE_VERSION\""
    echo
  done

  echo '########################################
# Override default *_VER variables above
source scripts/image_versions.src

if [ "$1" ]; then
  eval "$@"
fi
# End of file'
}
overwriteSrcFile > "$SRC_FILE"
