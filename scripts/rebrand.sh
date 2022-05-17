#!/usr/bin/env bash

path="."
tlOrig='gov'
orgOrig='va'
groupOrig='starter'
prefixOrig='dps'
githubOrgOrig='department-of-veterans-affairs'
repoNameOrig='lighthouse-di-starterkit-java'
githubOrg='thoughtworks'
tl='com'
org='thoughtworks'
group='starter'
prefix='tw'
repoName='starter-java'
nukeGit=n
dst=
clear=n

function usage {
  echo "$0 [--path <path>] [--dst <dest path> ] \\"
  echo "    [--repo <reponame>] [--gh-org <github Org name>] \\"
  echo "    [--tl <toplevel>] [--org <organization>] \\"
  echo "    [--orig-repo <reponame>] [--orig-gh-org <github Org name>] \\"
  echo "    [--orig-tl <original toplevel>] [--orig-org <original org>] \\"
  echo "    [--nuke-git] [--clear] [--test]"
  echo "  --path        path to process ($path)"
  echo "  --dst         copy to destination (no in-place mods) ($dst)"
  echo "  --repo        repository name ($repoName)"
  echo "  --gh-org      github organization name [or username] ($githubOrg)"
  echo "  --tl          top level package name ($tl)"
  echo "  --org         org level package name ($org)"
  echo "  --group       group level package name ($group)"
  echo "  --prefix      prefix for package name ($prefix)"
  echo "  --orig-repo   original repository name ($repoNameOrig)"
  echo "  --orig-gh-org original github organization name [or username] ($githubOrgOrig)"
  echo "  --orig-tl     original top level package name ($tlOrig)"
  echo "  --orig-org    original org level package name ($orgOrig)"
  echo "  --orig-group  original group level package name ($groupOrig)"
  echo "  --orig-prefix original prefix for package name ($prefixOrig)"
  echo "  --nuke-git    remove .git repository folder ($nukeGit)"
  echo "  --clear       clear destination directory of current contents"
  echo "  --test        copy to temporary directory to test"
  echo "  --help        display this help"
}

function fail {
  local msg=$*
  echo "${msg}"
  exit 1
}

while [ $# -gt 0 ]
do
  case $1 in
  --path) shift; path=$1;;
  --dst) shift; dst=$1;;
  --repo) shift; repoName=$1;;
  --test) dst=$(mktemp -d /tmp/rebrand.XXXXXX);;
  --gh-org) shift; githubOrg=$1;;
  --orig-gh-org) shift; githubOrgOrig=$1;;
  --tl) shift; tl=$1;;
  --org) shift; org=$1;;
  --group) shift; group=$1;;
  --prefix) shift; prefix=$1;;
  --orig-tl) shift; tlOrig=$1;;
  --orig-org) shift; orgOrig=$1;;
  --orig-group) shift; groupOrig=$1;;
  --orig-prefix) shift; prefixOrig=$1;;
  --nuke-git) nukeGit="y";;
  --clear) clear="y";;
  --help) usage; exit 0;;
  *) echo "Unknown argument(s): $*"; usage; exit 1;;
  esac
  shift;
done

if [ -n "${dst}" ]
then
  if [ ! -d "${dst}" ]
  then
    mkdir -p "${dst}" || exit 1
  else
    [[ "${clear}" == "y" ]] && rm -rf "${dst:?}"/{*,.*} 2> /dev/null
  fi
  cp -r "${path}" "${dst}"
  path="${dst}"
fi

[ -e "${path}"/.git ] && [ "${nukeGit}" = "y" ] && rm -rf "${path:?}"/.git
[ -e "${path}"/.git ] && [ "${nukeGit}" = "n" ] && echo "Local .git repository still exists, consider deleting..."

for subdir in buildSrc/build buildSrc/.gradle build .idea
do
  [ -e "${path}"/"${subdir}" ] && rm -rf "${path:?}"/"${subdir}"
done

githubOrgOrigLower=$(echo "${githubOrgOrig}" | tr '[:upper:]' '[:lower:]')
sedFile=$(mktemp /tmp/sed.XXXXXX) || exit 1
{
  echo "s:${tlOrig}\.${orgOrig}:${tl}.${org}:g"
  echo "s:${tlOrig}/${orgOrig}:${tl}/${org}:g"
  echo "s:${orgOrig}\.${tlOrig}:${org}.${tl}:g"
  echo "s:${githubOrgOrig}:${githubOrg}:g"
  echo "s:${githubOrgOrigLower}:${githubOrg}:g"
  echo "s:${repoNameOrig}:${repoName}:g"
  echo "s/DOCKER_REGISTRY: ${orgOrig}/DOCKER_REGISTRY: ${org}/g"
  echo "s:dockerRegistry=${orgOrig}:dockerRegistry=${org}:g"
  echo "s:${orgOrig}/di:${org}/di:g"
  echo "s:${orgOrig}/:${org}/:g"
  echo "s:${orgOrig}-di:${org}-di:g"
  echo "s:${prefixOrig}-:${prefix}-:g"
  echo "s:\"${tlOrig}\":\"${tl}\":g"
  echo "s:'${tlOrig}':'${tl}':g"
  echo "s:\"${orgOrig}\":\"${org}\":g"
  echo "s:'${orgOrig}':'${org}':g"
  echo "s:\"${groupOrig}\":\"${group}\":g"
  echo "s:'${groupOrig}':'${group}':g"
  echo "s:${orgOrig}:${org}:g"
} >> "${sedFile}"

pwd=$(pwd)
# assume other scripts are in same dir
binDir=$(dirname "$0")
"${binDir}"/alter-path.sh --path "${path}" \
  --tl "${tl}" \
  --org "${org}" \
  --group "${group}" \
  --orig-tl "${tlOrig}" \
  --orig-org "${orgOrig}" \
  --orig-group "${groupOrig}" \
  --orig-service "" \
  --orig-resource ""
"${binDir}"/apply-sed.sh --sed "${sedFile}" --tree "${path}"

# Initialize git repository if it doesn't already exist
[ -e "${path}"/.git ] || (cd "${path}" && git init && git add .)

cd "${pwd}"  || fail "unable to change directory to [${pwd}]"
rm "${sedFile}"
echo "new repository is in ${path}"
echo "make sure you commit changes before building"
echo "git add -u . && git commit -m \"initial starting point\" && git branch -M main"
