#!/bin/sh
cd app || exit

python -m streamlit run main.py --server.headless true --server.enableCORS=false --server.enableXsrfProtection=false
