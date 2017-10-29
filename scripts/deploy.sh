#!/bin/bash -e
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
docker run --rm -it -v ~/.ssh_docker:/root/.ssh -v "$SCRIPT_DIR/..":/docs squidfunk/mkdocs-material gh-deploy 

