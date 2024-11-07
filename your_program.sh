#!/bin/sh

set -e # Exit early if any commands fail

(
  cd "$(dirname "$0")" # Ensure compile steps are run within the repository directory
  mvn -B package -Ddir=/tmp/ktorus-build-http-server
)

exec java -jar /tmp/ktorus-build-http-server/ktorus.jar "$@"
