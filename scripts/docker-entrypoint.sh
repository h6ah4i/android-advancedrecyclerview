#!/bin/sh
set -e

if [ -d /tmp/.ssh ]; then
  cp -R /tmp/.ssh /root/.ssh
  chmod 700 /root/.ssh
  chmod 644 /root/.ssh/id_rsa.pub
  chmod 600 /root/.ssh/id_rsa
fi

exec "$@"
