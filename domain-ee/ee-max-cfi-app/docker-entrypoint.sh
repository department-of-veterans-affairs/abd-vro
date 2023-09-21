#!/bin/sh
exec ddtracerun uvicorn python_src.api:app --host 0.0.0.0 --port 8130
