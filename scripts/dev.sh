#!/bin/bash -e
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd "$SCRIPT_DIR/.."

docker build -t android-advancedrecyclerview-gh-page/mkdocs "$SCRIPT_DIR"
docker run --rm -it -p 8000:8000 -v "$SCRIPT_DIR/..":/docs android-advancedrecyclerview-gh-page/mkdocs:latest
