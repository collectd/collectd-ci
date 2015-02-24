#!/bin/sh

# usage: make-dist-archive.sh <revision>

set -xe

test "x$(id -un)" = "xjenkins"
test -n "$1"

REF="$1"
REPO="/usr/src/collectd"

test -d $REPO || git clone https://github.com/collectd/collectd.git $REPO
cd $REPO

git config --local remote.origin.fetch "+refs/pull/*/head:refs/remotes/origin/pr/*"
git fetch --all --quiet
git reset --hard
git checkout --force "$REF"

./clean.sh
./build.sh
./configure
make dist-gzip

COLLECTD_BUILD=$(./version-gen.sh)
TARBALL="${REPO}/collectd-${COLLECTD_BUILD}.tar.gz"

test -f $TARBALL

cat > jenkins-env.sh << EOF
COLLECTD_BUILD=$COLLECTD_BUILD
GIT_COMMIT=$GIT_COMMIT
GIT_BRANCH=$GIT_BRANCH
TARBALL=/tmp/collectd-$COLLECTD_BUILD/$(basename $TARBALL)
EOF

mkdir -p /tmp/collectd-$COLLECTD_BUILD
mv $TARBALL /tmp/collectd-$COLLECTD_BUILD
