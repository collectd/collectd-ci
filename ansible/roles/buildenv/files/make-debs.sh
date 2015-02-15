#!/bin/sh

# Usage: make-debs.sh <distro> <arch> <tarball>

set -xe

test "x$(id -un)" = "xjenkins"
test -n "$1"
test "$2" = "i386" || test "$2" = "amd64"
test -n "$3"
test -f "$3"

case "$1" in
  precise|trusty|squeeze|wheezy|jessie)
    DIST="$1"
    ARCH="$2"
    TARBALL="$3"
  ;;

  *)
    echo "unknown distro $DIST"
    exit 1
  ;;
esac

REPO="/usr/src/pkg-debian"
COLLECTD_BUILD=$(cd /usr/src/collectd && ./version-gen.sh)

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
  gbp import-orig -u $COLLECTD_BUILD --no-merge $TARBALL
fi

git checkout -f origin/nightlies/$DIST

if [ $(git branch --list "nightlies/${DIST}-${COLLECTD_BUILD}" | wc -l) -eq "1" ]; then
  git branch -D "nightlies/${DIST}-${COLLECTD_BUILD}"
fi
git checkout -B "nightlies/${DIST}-${COLLECTD_BUILD}"
git rm debian/patches/*.dpatch
echo > debian/patches/00list
git add debian/patches/00list
git commit -m "remove dpatches"
dch -D unstable -v "${COLLECTD_BUILD}-1~${DIST}" "nightly build"
git add debian/changelog
git commit -m "automatic changelog update"
git merge --no-edit upstream/$COLLECTD_BUILD

export GIT_PBUILDER_OUTPUT_DIR="/srv/build_artefacts/${DIST}-${ARCH}"
rm -fr $GIT_PBUILDER_OUTPUT_DIR
mkdir -p $GIT_PBUILDER_OUTPUT_DIR

git-buildpackage --git-pbuilder --git-dist=$DIST --git-arch=$ARCH --git-debian-branch="nightlies/${DIST}-${COLLECTD_BUILD}" --git-pbuilder-options="--aptcache /var/cache/pbuilder/aptcache/$DIST"

