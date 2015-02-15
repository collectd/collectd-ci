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
make dist-bzip2

test -f $REPO/collectd-$(./version-gen.sh).tar.bz2
