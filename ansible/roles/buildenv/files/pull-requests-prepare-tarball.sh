#!/bin/sh

set -xe

test "x$(id -un)" = "xjenkins"

cd "${WORKSPACE}"

test -n "$BUILD_GIT_BRANCH"

git config --local core.sparsecheckout true
git config --local user.email "ci@collectd.org"
git config --local user.name "Jenkins"

git reset --hard
rm -f env-*.sh

git checkout -f target/master
git merge --ff --no-edit --log $BUILD_GIT_BRANCH || (git diff && exit 1)
git show --stat HEAD

for shellscript in *.sh; do checkbashisms -n $shellscript || exit 1; done

./clean.sh
./build.sh
./configure
make dist-gzip

COLLECTD_BUILD="$(./version-gen.sh)"
TARBALL="collectd-$COLLECTD_BUILD.tar.gz"
PULL_REQUEST="$(basename $BUILD_GIT_BRANCH)"
test -f "$TARBALL"
test -n "$BUILD_NUMBER"
test -n "$PULL_REQUEST"

cat << EOF > "env-${BUILD_GIT_COMMIT}.sh"
COLLECTD_BUILD=$COLLECTD_BUILD
TARBALL=$TARBALL
TARBALL_BUILD_NUMBER=$BUILD_NUMBER
PULL_REQUEST=$PULL_REQUEST
EOF
