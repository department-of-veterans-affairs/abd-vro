#!/usr/bin/env bash


function usage {
  echo "$0 [--image] [--container] [--volume] [--dangle]"
  echo "  --image     remove dangling images"
  echo "  --container remove dangling containers"
  echo "  --volume    remove dangling volumes"
  echo "  --dangle    remove dangling images (old method)"
  echo "  --help      display this help"
}

dockerBinary=$(which docker)

function remove_danglers {
  DANGLING_IMAGES=$($dockerBinary images -f "dangling=true" -q)
  if [[ -n "$DANGLING_IMAGES" ]]; then ($dockerBinary rmi "${DANGLING_IMAGES}"); fi
}


while [ $# -gt 0 ]
do
  case $1 in
  --image) $dockerBinary image prune -f;;
  --container) $dockerBinary container prune -f;;
  --volume) $dockerBinary volume prune -f;;
  --dangle) remove_danglers;;
  --help) usage; exit 0;;
  *) usage; exit 1;;
  esac
  shift
done

exit 0
