#! /bin/sh


file="/policy.rego"
files=""
url="opa:8181"
path="/v1/policies/authz"
retries=10
delay=10
processed="n"
save="n"

function usage {
  if [ $# -gt 0 ]
  then
    echo "Unknown options [$*]"
  fi
  echo "$0 [--url <OPA base URL>] [--file <rego file>] [--glob <pattern>] [--path <policy path>] [--retries <#>] [--delay <#>] --save"
  echo "  --url         base url of OPA server (${url})"
  echo "  --file        Rego policy file to process (${file}) (can be multiple)"
  echo "  --glob        file glob specifying policy file pattern to construct policy.rego"
  echo "  --path        OPA document path to install policy file (${path})"
  echo "  --retries     how many retries while we wait for OPA server (${retries})"
  echo "  --delay       delay in between retries (${delay})"
  echo "  --save        save the temporary constructed rego policy file"
  echo "  --help        display this help"
  echo "  NOTE: file containing header data must come first in arg list"
}

function process_file {
  local output=$1
  shift;

  while [ $# -gt 0 ]
  do
    cat "${1}" >> "${output}"
    shift
  done
  processed="y"
}

tmpFile=$(mktemp /tmp/rego.policy.XXXXXX)

while [ $# -gt 0 ]
do
  case $1 in
  --url) shift; url=$1;;
  --file) shift; process_file "${tmpFile}" ${1};;
  --glob) shift; process_file "${tmpFile}" ${1};;
  --path) shift; path=$1;;
  --retries) shift; retries=$1;;
  --delay) shift; delay=$1;;
  --save) save="y";;
  --help) usage; exit 0;;
  *) usage $*; exit 1;;
  esac
  shift;
done

if [ "${processed}" != "y" ]
then
  process_file "${tmpFile}" "${file}"
fi

result=1
while [[ ${result} -ne 0 ]] && [[ ${retries} -gt 0 ]]
do
  echo "Installing rego: delay [${delay}] retries [${retries}]"
  sleep ${delay}
  curl -s -X PUT --data-binary @"${tmpFile}" ${url}${path}
  result=$?
  retries=$(( ${retries}-1 ))
done

[ "${result}" ] || echo "Install failed."

[ "${save}" = "y" ] || rm "${tmpFile}"
