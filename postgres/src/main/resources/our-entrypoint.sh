#!/bin/sh
set -v

sudo -u root /init-folder.sh "${PGDATA}"

# Run the container's original ENTRYPOINT
exec docker-entrypoint.sh postgres
