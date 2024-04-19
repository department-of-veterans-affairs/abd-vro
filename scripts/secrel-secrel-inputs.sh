#!/usr/bin/env bash

# This script is only used as the secrel-inputs step in the Secrel GH workflow

source scripts/image_vars.src
IMAGE_LIST="[]"

# This case captures manual dispatches where publishing is disabled
if [ "${{ needs.publish-to-ghcr.result }}" == 'skipped' ]; then

  IMAGE_LIST="["

  if [ "${{inputs.image_name}}" == 'all' ]; then
    for PREFIX in "${VAR_PREFIXES_ARR[@]}"; do
      IMG_NAME="$(getVarValue "${PREFIX}" _IMG)"
      GHCR_PATH="ghcr.io/${{github.repository}}/${IMG_NAME}"
      IMAGE_LIST+="\"${GHCR_PATH}:latest\","
    done

    # Remove the trailing comma
    IMAGE_LIST=${IMAGE_LIST%?}
  else
    IMG_TAG="${{inputs.image_tag}}"
    IMG_NAME="$(getVarValue "$(bashVarPrefix "${{inputs.image_name}}")" _IMG)"
    GHCR_PATH="ghcr.io/${{github.repository}}/${IMG_NAME}"
    IMAGE_LIST+="\"${GHCR_PATH}:${IMG_TAG}\""
  fi

  IMAGE_LIST+="]"

# This case captures when the workflow is run automatically on PR merge along
# with manual dispatches where publishing is disabled
elif [ "${{needs.publish-to-ghcr.result}}" == 'success' ]; then
  IMAGE_LIST=${{needs.publish-to-ghcr.outputs.vro-images}}
fi

# Throw an error if the IMAGE_LIST variable is an empty array
if [ "${IMAGE_LIST}" == "[]" ]; then
  exit 1
fi

echo "image_list=${IMAGE_LIST}" >> "$GITHUB_OUTPUT"
