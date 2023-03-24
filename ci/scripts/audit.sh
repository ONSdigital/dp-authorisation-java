#!/bin/bash -eux

cwd=$(pwd)

pushd $cwd/dp-authorisation-java
  make audit
popd