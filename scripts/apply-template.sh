#!/usr/bin/env bash

template="resource"
url="gh:department-of-veterans-affairs/lighthouse-di-starterkit-java.git"
path="."
tag="release-0.1.11"

function usage {
  echo "$0 [--template <template>] [--repo <url>] [--path <path>] [--tag <git tag>]"
  echo "  --template   name of template to generate (${template})"
  echo "  --repo       git repository holding template (${url})"
  echo "  --path       process template files into path (${path})"
  echo "  --tag        tag (release) version of template (${tag})"
}

function apply_template {
  local url=$1
  local tag=$2
  local template=$3
  local target=$4

  cookiecutter "${url}" --directory templates/"${template}" --checkout "${tag}" -o "${target}"
}

while [ $# -gt 0 ]
do
  case $1 in
  --template) shift; template=$1;;
  --repo) shift; url=$1;;
  --path) shift; path=$1;;
  --tag) shift; tag=$1;;
  --help) usage; exit 0;;
  *) usage; exit 1;;
  esac
  shift;
done

apply_template "${url}" "${tag}" "${template}" "${target}"
