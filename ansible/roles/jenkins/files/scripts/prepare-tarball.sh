#!/bin/sh

set -xe

test "x$(id -un)" = "xjenkins"

./build.sh
./configure
make -s dist-gzip

COLLECTD_BUILD="$(./version-gen.sh)"
TARBALL="collectd-$COLLECTD_BUILD.tar.gz"

test -f "$TARBALL"
test -n "$BUILD_NUMBER"
test -n "$BUILD_GIT_COMMIT"

cat << EOF > "env-${BUILD_GIT_COMMIT}.sh"
COLLECTD_BUILD=$COLLECTD_BUILD
GIT_COMMIT=$GIT_COMMIT
GIT_BRANCH=$GIT_BRANCH
TARBALL=$TARBALL
TARBALL_BUILD_NUMBER=$BUILD_NUMBER
EOF
