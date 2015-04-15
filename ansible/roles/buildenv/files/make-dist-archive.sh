#!/bin/sh

# usage: make-dist-archive.sh <revision>

set -xe

test "x$(id -un)" = "xjenkins"
test -n "$1"

REF="$1"

./clean.sh
./build.sh
./configure
make dist-bzip2

COLLECTD_BUILD=$(./version-gen.sh)
TARBALL="collectd-${COLLECTD_BUILD}.tar.bz2"

test -f $TARBALL

git show "${GIT_BRANCH}:contrib/redhat/collectd.spec" > collectd.spec

cat > "${WORKSPACE}/env.sh" << EOF
COLLECTD_BUILD=$COLLECTD_BUILD
GIT_COMMIT=$GIT_COMMIT
GIT_BRANCH=$GIT_BRANCH
TARBALL=$(basename $TARBALL)
EOF

# then use "copy artefact plugin" to pass down the following files to
# downstream jobs: $TARBALL jenkins-env.sh collectd.spec
