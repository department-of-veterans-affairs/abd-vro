#!/usr/bin/env bash

runpath=$(dirname "$0")
script=
file=""
tree=""
path=""
preserve=""
debug="n"

function usage {
  echo "$0 [--sed <script>] [--preserve <ext>] [--file <file>] [--path <path>] [--tree <tree>]"
  echo "  --sed         sed script to execute (${script})"
  echo "  --preserve    preserve translated files with this extension for debugging ($preserve)"
  echo "  --file        file to process (${file})"
  echo "  --path        process all files in path non-recursively (${path})"
  echo "  --tree        process all files in tree recursively (${tree})"
  echo "  --debug       log files being processed"
  echo "  --help        display this help"
  echo "  NOTE: files are processed in the order specified on the command line"
  echo "        The --sed script and --preserve flag must be specified before any files/paths"
}

function process_file {
  local sedScript=$1
  local file=$2

  if [[ $preserve != "" ]] || [[ $debug == "y" ]]
  then
    echo "processing [${file}]"
  fi
  [ -e "${file}" ] && sed -i "${preserve}" -f "${sedScript}" "${file}"
}

function find_script {
  local file=$1
  if [ ! -e "${file}" ]
  then
    # echo "no path to ${file}, searching in [${runpath}]"
    file="${runpath}"/"${file}"
    if [ ! -e "${file}" ]
    then
      echo "no path to sed script [${file}], exiting..."
      exit 1
    fi
  fi

  echo "${file}"
}

function process_tree {
  local sedScript=$1
  local path=$2
  shift 2

  [[ $debug == "y" ]] && echo "Processing tree: [${path}] sed: [${sedScript}]"
  if [ -d "${path}" ]
  then
    find "${path}" \( -path "${path}"/.gradle -o -path "${path}"/gradle \) -prune -false -o -type f  "$@" -print | while read -r i
    do
      process_file "${sedScript}" "${i}"
    done
  fi
}

function process_path {
  local sedScript=$1
  local path=$2
  [[ $debug == "y" ]] && echo "Processing path: [${path}] sed: [${sedScript}]"
  process_tree "${sedScript}" "${path}" -depth 1
}

function verify_sed_script {
  if [[ -z "${script}" ]]
  then
    echo "Error: --sed option must be specified before file/path/tree options"
    exit 1
  fi
}

while [ $# -gt 0 ]
do
  case $1 in
  --sed) shift; script=$(find_script "$1");;
  --file) shift; verify_sed_script; process_file "${script}" "$1";;
  --tree) shift; verify_sed_script; process_tree "${script}" "$1";;
  --path) shift; verify_sed_script; process_path "${script}" "$1";;
  --preserve) shift; preserve=$1;;
  --debug) debug="y";;
  --help) usage; exit 0;;
  *) echo "Error: Unknown argument(s): $*"; usage; exit 1;;
  esac
  shift;
done

