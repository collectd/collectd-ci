#!/bin/sh

set -xe

test "x$(id -un)" = "xjenkins"

. "${WORKSPACE}/s3repo.sh"

test -n "$PKGDIR"
test -n "$BRANCH"
test -n "$DIST"
test -n "$ARCH"
test -n "$COLLECTD_BUILD"

REPO="dists/${DIST}/${BRANCH}/binary-${ARCH}"

test -d "$PKGDIR/$REPO"

(cd "$PKGDIR" && dpkg-scanpackages "$REPO" > "$REPO/Packages")
gzip -9vc "$PKGDIR/$REPO/Packages" > "$PKGDIR/$REPO/Packages.gz"
bzip2 -f9vk "$PKGDIR/$REPO/Packages"

cat << EOF > "$PKGDIR/$REPO/status.json"
{
  "branch": "${BRANCH}",
  "dist": "${DIST}-${ARCH}",
  "collectd_build": "${COLLECTD_BUILD}"
}
EOF

test -f ~/.s3cfg

s3cmd --acl-public --delete-removed --no-progress sync "$PKGDIR/$REPO/" "s3://collectd/deb/$REPO/"
