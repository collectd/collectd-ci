#!/bin/sh

set -xe

test "x$(id -un)" = "xjenkins"

. "${WORKSPACE}/s3repo.sh"

test -n "$PKGDIR"
test -n "$BRANCH"
test -n "$DIST"

REPO="$PKGDIR/$BRANCH/$DIST"

test -d $REPO

if test "$DIST" = "epel-5-i386" || test "$DIST" = "epel-5-x86_64"; then
  createrepo -s sha "$REPO"
else
  createrepo "$REPO"
fi

test -f ~/.s3cfg

s3cmd --acl-public --delete-removed --no-progress sync "$REPO/" "s3://collectd/rpm/$BRANCH/$DIST/"
