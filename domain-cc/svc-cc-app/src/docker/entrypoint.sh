#!/bin/sh

# TODO: I don't think --reload is needed when running in a container
exec uvicorn python_src.api:app --host 0.0.0.0 --port 8120
