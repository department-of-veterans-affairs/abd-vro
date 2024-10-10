#!/bin/sh

# create and use virtual environment
#python -m venv .venv
#source .venv/bin/activate

# install dependencies
#pip install -e .
#pip install -r src/requirements.txt

# run streamlit

#cd src/app
python -m streamlit run app.py --server.headless true
