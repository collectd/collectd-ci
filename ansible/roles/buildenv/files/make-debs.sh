#!/bin/sh

# Usage: make-debs.sh <distro> <arch>

set -xe

test "x$(id -un)" = "xjenkins"
test -n "$1"
test "$2" = "i386" || test "$2" = "amd64"

case "$1" in
  precise|trusty|squeeze|wheezy|jessie)
    DIST="$1"
    ARCH="$2"
  ;;

  *)
    echo "unknown distro $DIST"
    exit 1
  ;;
esac

# This file, as well as $TARBALL, comes from jenkins' upstream job, using the
# "copy artefact plugin".
. "${WORKSPACE}/env.sh"

test -n "$COLLECTD_BUILD"
test -n "$GIT_BRANCH"
test -n "$TARBALL"
test -f "${WORKSPACE}/${TARBALL}"

REPO="/usr/src/pkg-debian"
BRANCH=$(basename $GIT_BRANCH)

test -d $REPO || git clone https://github.com/mfournier/pkg-debian.git "$REPO"
cd "$REPO"

git reset --hard
git checkout -f master
git fetch --all --quiet

if ! git show upstream > /dev/null 2>&1; then
  git config --global user.name "Jenkins"
  git config --global user.email "jenkins@buildenv"

  git symbolic-ref HEAD refs/heads/upstream
  git rm --cached -r .
  git commit --allow-empty -m'Initial upstream branch'
  git checkout -f master
fi

if [ $(git tag -l upstream/$COLLECTD_BUILD | wc -l) -eq "0" ]; then
  gbp import-orig -u $COLLECTD_BUILD --no-merge "${WORKSPACE}/${TARBALL}"
fi

git checkout -f "origin/nightlies/${BRANCH}/${DIST}"

DEBIAN_BRANCH="nightlies/${BRANCH}/${DIST}-${COLLECTD_BUILD}"

if [ $(git branch --list $DEBIAN_BRANCH | wc -l) -eq "1" ]; then
  git branch -D $DEBIAN_BRANCH
fi
git checkout -B $DEBIAN_BRANCH
git rm debian/patches/*.dpatch
echo > debian/patches/00list
git add debian/patches/00list
git commit -m "remove dpatches"
dch -D unstable -v "${COLLECTD_BUILD}-1~${DIST}" "nightly build"
git add debian/changelog
git commit -m "automatic changelog update"
git merge --no-edit upstream/$COLLECTD_BUILD

export GIT_PBUILDER_OUTPUT_DIR="/srv/build_artefacts/$BRANCH/${DIST}-${ARCH}"
rm -fr $GIT_PBUILDER_OUTPUT_DIR
mkdir -p $GIT_PBUILDER_OUTPUT_DIR

git-buildpackage --git-pbuilder --git-dist=$DIST --git-arch=$ARCH --git-debian-branch=$DEBIAN_BRANCH --git-pbuilder-options="--aptcache /var/cache/pbuilder/aptcache/$DIST"

