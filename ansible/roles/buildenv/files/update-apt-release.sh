#!/bin/sh

set -xe

test "x$(id -un)" = "xjenkins"

. "${WORKSPACE}/s3repo.sh"

test -n "$PKGDIR"
test -n "$DIST"

DISTDIR="$PKGDIR/dists/${DIST}"

test -d "$DISTDIR"

rm -f "$DISTDIR/Release" "$DISTDIR/Release.gpg"

apt-ftparchive release "$DISTDIR" > "$DISTDIR/Release"
gpg --detach-sign --armor --batch --default-key ci@collectd.org --output "$DISTDIR/Release.gpg" "$DISTDIR/Release"

test -f ~/.s3cfg

s3cmd --acl-public --no-progress put "$DISTDIR/Release" "$DISTDIR/Release.gpg" "s3://collectd/deb/dists/$DIST/"
