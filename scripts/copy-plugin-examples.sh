#!/usr/bin/env bash


src=${1:-../lighthouse-di-starter-boot/plugins}
dst=./buildSrc
groovyPath='src/main/groovy'

if [ ! -d "${dst}" ]
then
  echo "No [$dst] path exists"
  exit 1
fi

for i in "${src}"/"${groovyPath}"/*.gradle
do
  file=$(basename "${i}")
  echo "[${i}] [${file}]"
  cp "${i}" "${dst}"/"${groovyPath}"/"${file/starter/local}".example
done

cp "${src}"/README.md "${dst}"
