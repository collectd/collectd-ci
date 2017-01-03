#!/bin/sh

set -xe

test "x$(id -un)" = "xjenkins"

. "${WORKSPACE}/s3repo.sh"

test -n "$PKGDIR"
test -n "$BRANCH"
test -n "$DIST"
test -n "$COLLECTD_BUILD"

REPO="$PKGDIR/$BRANCH/$DIST"

test -d $REPO

createrepo "$REPO"
gpg --detach-sign --armor --digest-algo SHA512 --batch --default-key ci@collectd.org "$REPO/repodata/repomd.xml"

cat << EOF > "$REPO/status.json"
{
  "branch": "${BRANCH}",
  "dist": "${DIST}",
  "collectd_build": "${COLLECTD_BUILD}"
}
EOF

test -f ~/.s3cfg

s3cmd --acl-public --delete-removed --no-progress sync "$REPO/" "s3://collectd/rpm/$BRANCH/$DIST/"
