#!/bin/bash
set -e

if ! command -v brew &> /dev/null
then
    echo "Installing homebrew"
    curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh
fi

echo "Homebrew installed. Running brew bundle"
brew bundle -v

echo "Installing pre-commit modules"
pre-commit install --hook-type pre-push
pre-commit install --hook-type commit-msg
echo "All done!"
