#!/bin/bash


minHeader=1
maxHeader=2
update="n"

function usage {
  echo "${0} [--min <level>] [--max <level>] [--update] file ..."
  echo "  --min    minimum header level (${minHeader})"
  echo "  --max    maximum header level (${maxHeader})"
  echo "  --update strip off existing toc, up to initial divider mark '----'"
}

# It will read each input file and:
#
# 0. Strip out existing TOC content, up to the first divider `----` if `--update` option is specified
# 1. Extract only the headers via grep (using $1 and $2 as first and last heading level)
# 2. Extract the header text via sed and created ':' separated records of the form '###:Full Text:Full Text'
# 3. Compose each TOC line via awk by replacing '#' with '  ' and stripping spaces and caps of reference

function strip_file {
  local file=$1
  local dest=$2

  # sed has an issue if the '----' marker is the first line, it deletes the whole file
  (echo; cat "${file}") | sed '1,/^----$/d' > "${dest}"
}

function process_file {
  local file=$1
  local min=$2
  local max=$3
  local strip=$4
  local tmpFile="/tmp/toc-input"
  local tmpOutput="/tmp/toc"

  tmpFile=$(mktemp /tmp/toc-input.XXXXXXXX)
  tmpOutput=$(mktemp /tmp/toc.XXXXXXXX)

  if [ "${strip}" = "y" ]
  then
    strip_file "${file}" "${tmpFile}"
  else
    cp "${file}" "${tmpFile}"
  fi

  grep -E "^#{${min:-1},${max:-2}} " "${tmpFile}"| \
  sed -E 's/(#+) (.+)/\1:\2:\2/g' | \
  awk -F ":" '{ gsub(/#/,"  ",$1); gsub(/[ ]/,"-",$3); print $1 "- [" $2 "](#" tolower($3) ")" }' \
  > "${tmpOutput}"

  ( cat "${tmpOutput}"; echo; echo "----"; cat "${tmpFile}" ) > "${file}"

  rm "${tmpFile}"
  rm "${tmpOutput}"
}

while [ $# -gt 0 ]
do
  case $1 in
  --min) shift; minHeader=$1;;
  --max) shift; maxHeader=$1;;
  --update) update="y";;
  --help) usage; exit 0;;
  --*) echo "Invalid option [$1]"; usage; exit 1;;
  *) process_file "${1}" "${minHeader}" "${maxHeader}" "${update}";;
  esac
  shift
done
