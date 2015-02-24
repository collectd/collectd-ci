#!/bin/sh

# Usage: make-rpms.sh <distro>

set -xe

test "x$(id -un)" = "xjenkins"
test -n "$1"

case "$1" in
  epel-5-i386|epel-5-x86_64|epel-6-i386|epel-6-x86_64|epel-7-x86_64)
    DIST="$1"
    RPMBUILD="/var/lib/jenkins/rpmbuild"
    SRC="/usr/src/collectd"
  ;;

  *)
    echo "unknown distro $DIST"
    exit 1
  ;;
esac

. $SRC/jenkins-env.sh

test -n "$COLLECTD_BUILD"
test -n "$GIT_BRANCH"
test -n "$TARBALL"
test -f $TARBALL
test -d "/var/cache/mock/$DIST"

rm -fr "$RPMBUILD"
for dir in BUILD BUILDROOT RPMS SOURCES SPECS SRPMS; do
  mkdir -p "$RPMBUILD/$dir"
done

cp "$TARBALL" "$RPMBUILD/SOURCES/"
cp /usr/src/collectd/contrib/redhat/collectd.spec "$RPMBUILD/SPECS/"

sed -ri "s/^(Version:\s+).+/\1$COLLECTD_BUILD/" "$RPMBUILD/SPECS/collectd.spec"

rpmbuild -bs "$RPMBUILD/SPECS/collectd.spec"

RESULTDIR="/srv/build_artefacts/$GIT_BRANCH/$DIST"

rm -fr "$RESULTDIR"
mkdir -p "$RESULTDIR"

mock --verbose --cleanup-after --rpmbuild_timeout=600 -r "$DIST" --rebuild $RPMBUILD/SRPMS/collectd-${COLLECTD_BUILD}-*.src.rpm --resultdir="$RESULTDIR"

