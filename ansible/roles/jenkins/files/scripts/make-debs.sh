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
# "copy artifact plugin".
. "${WORKSPACE}/env.sh"

test -n "$COLLECTD_BUILD"
test -n "$GIT_BRANCH"
test -n "$TARBALL"
test -f "${WORKSPACE}/${TARBALL}"

REPO="$WORKSPACE/pkg-debian"
BRANCH=$(basename $GIT_BRANCH)
PKGDIR="/srv/build_artifacts/deb"
export DEBEMAIL='ci@collectd.org'

test -d $REPO || git clone https://github.com/mfournier/pkg-debian.git "$REPO"
cd "$REPO"

git reset --hard
git checkout -f master
git fetch --all --quiet

if ! git show upstream > /dev/null 2>&1; then
  git config --local user.name "Jenkins"
  git config --local user.email "jenkins@buildenv"

  git symbolic-ref HEAD refs/heads/upstream
  git rm --cached -r .
  git commit --allow-empty -m'Initial upstream branch'
  git checkout -f master
fi

if [ $(git tag -l upstream/$COLLECTD_BUILD | wc -l) -eq "0" ]; then
  gbp import-orig --no-interactive --no-merge "${WORKSPACE}/${TARBALL}"
fi

git checkout -f "origin/nightlies/${BRANCH}/${DIST}"

DEBIAN_BRANCH="nightlies/${BRANCH}/${DIST}-${COLLECTD_BUILD}"
PKG_VERSION="${COLLECTD_BUILD}-1~${DIST}"

if [ $(git branch --list $DEBIAN_BRANCH | wc -l) -eq "1" ]; then
  git branch -D $DEBIAN_BRANCH
fi
git checkout -B $DEBIAN_BRANCH
rm -f debian/patches/*patch
echo > debian/patches/00list
echo > debian/patches/series
git add debian/patches/
git commit -m "remove (d)patches"

dch -D unstable -v $PKG_VERSION "nightly build"
git add debian/changelog
git commit -m "automatic changelog update"
git merge --no-edit upstream/$COLLECTD_BUILD

export GIT_PBUILDER_OUTPUT_DIR="$PKGDIR/dists/${DIST}/${BRANCH}/binary-${ARCH}"
rm -fr $GIT_PBUILDER_OUTPUT_DIR
mkdir -p $GIT_PBUILDER_OUTPUT_DIR

sudo DIST=$DIST ARCH=$ARCH cowbuilder --update --distribution $DIST --architecture $ARCH --basepath /var/cache/pbuilder/base-$DIST-$ARCH.cow
gbp buildpackage --git-pbuilder --git-dist=$DIST --git-arch=$ARCH --git-debian-branch=$DEBIAN_BRANCH --git-pbuilder-options="--aptcache /var/cache/pbuilder/aptcache/$DIST" --git-no-create-orig --git-tarball-dir="$WORKSPACE"

debsign -k$DEBEMAIL "${GIT_PBUILDER_OUTPUT_DIR}/collectd_${PKG_VERSION}_${ARCH}.changes"

cp ${WORKSPACE}/collectd_${PKG_VERSION}_*.build "$GIT_PBUILDER_OUTPUT_DIR"

cat > "${WORKSPACE}/s3repo.sh" << EOF
ARCH=$ARCH
BRANCH=$BRANCH
COLLECTD_BUILD=$COLLECTD_BUILD
DIST=$DIST
PKGDIR=$PKGDIR
EOF

