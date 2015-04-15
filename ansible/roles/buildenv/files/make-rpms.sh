#!/bin/sh

# Usage: make-rpms.sh <distro>

set -xe

test "x$(id -un)" = "xjenkins"
test -n "$1"

case "$1" in
  epel-5-i386|epel-5-x86_64|epel-6-i386|epel-6-x86_64|epel-7-x86_64)
    DIST="$1"
    RPMBUILD="/var/lib/jenkins/rpmbuild"
  ;;

  *)
    echo "unknown distro $DIST"
    exit 1
  ;;
esac

# This file, as well as $TARBALL and collectd.spec, comes from jenkins'
# upstream job, using the "copy artefact plugin".
. "${WORKSPACE}/env.sh"

test -n "$COLLECTD_BUILD"
test -n "$GIT_BRANCH"
test -n "$TARBALL"
test -f "${WORKSPACE}/${TARBALL}"
test -f "${WORKSPACE}/collectd.spec"
test -d "/var/cache/mock/$DIST"

BRANCH=$(basename $GIT_BRANCH)

rm -fr "$RPMBUILD"
for dir in BUILD BUILDROOT RPMS SOURCES SPECS SRPMS; do
  mkdir -p "$RPMBUILD/$dir"
done

cp -f "${WORKSPACE}/${TARBALL}" "$RPMBUILD/SOURCES/"
cp -f "${WORKSPACE}/collectd.spec" "$RPMBUILD/SPECS/"

sed -ri "s/^(Version:\s+).+/\1$COLLECTD_BUILD/" "$RPMBUILD/SPECS/collectd.spec"

rpmbuild -bs "$RPMBUILD/SPECS/collectd.spec"

RESULTDIR="/srv/build_artefacts/$BRANCH/$DIST"

rm -fr "$RESULTDIR"
mkdir -p "$RESULTDIR"

mock --verbose --cleanup-after --rpmbuild_timeout=600 -r "$DIST" --rebuild $RPMBUILD/SRPMS/collectd-${COLLECTD_BUILD}-*.src.rpm --resultdir="$RESULTDIR"

