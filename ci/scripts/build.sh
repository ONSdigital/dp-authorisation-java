#!/bin/bash -eux

cwd=$(pwd)

pushd $cwd/dp-authorisation-java
  make build
popd