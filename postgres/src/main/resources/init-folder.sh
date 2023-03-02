#!/bin/sh
# After K8S mounts the persistent volume (/persist/postgres), run this file as root using sudo.
# When deployed to LHDI, PGDATA is under /persist/postgres

PGDATA=$1

if [ "${PGDATA}" ]; then
  echo "${USER}: Ensuring ${PGDATA} folder is owned by postgres"
  mkdir -p "${PGDATA}"
  chown postgres:postgres "${PGDATA}"
else
  echo "Env variable PGDATA is not set -- using the default data folder."
fi
