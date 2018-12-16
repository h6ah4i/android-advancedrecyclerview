#!/bin/bash -e
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd "$SCRIPT_DIR/.."

docker build -t android-advancedrecyclerview-gh-page/mkdocs "$SCRIPT_DIR"
docker run --rm -it -v ~/.ssh_docker:/tmp/.ssh:ro -v "$SCRIPT_DIR/..":/docs android-advancedrecyclerview-gh-page/mkdocs:latest gh-deploy
