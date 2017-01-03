#!/bin/sh

set -xe

test "x$(id -un)" = "xjenkins"

. "${WORKSPACE}/s3repo.sh"

test -n "$PKGDIR"
test -n "$DIST"

DISTDIR="$PKGDIR/dists/${DIST}"
test -d "$DISTDIR"

RELEASE="$(mktemp)"
test -f "$RELEASE"

rm -f "$DISTDIR/Release" "$DISTDIR/Release.gpg"

apt-ftparchive	-o "APT::FTPArchive::Release::Architectures"="amd64 i386" \
		-o "APT::FTPArchive::Release::Components"="master collectd-5.7 collectd-5.6 collectd-5.5" \
		-o "APT::FTPArchive::Release::Codename=$DIST" \
		release "$DISTDIR" > "$RELEASE"
mv "$RELEASE" "$DISTDIR/Release"
chmod 0644 "$DISTDIR/Release"
gpg --detach-sign --armor --digest-algo SHA512 --batch --default-key ci@collectd.org --output "$DISTDIR/Release.gpg" "$DISTDIR/Release"

test -f ~/.s3cfg

s3cmd --acl-public --no-progress put "$DISTDIR/Release" "$DISTDIR/Release.gpg" "s3://collectd/deb/dists/$DIST/"
