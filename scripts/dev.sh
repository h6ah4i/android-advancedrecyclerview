#!/bin/bash -e
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
docker run --rm -it -p 8000:8000 -v "$SCRIPT_DIR/..":/docs squidfunk/mkdocs-material
