#!/bin/sh
cd app || exit

python -m streamlit run main.py --server.headless true --server.enableWebsocketCompression=false
