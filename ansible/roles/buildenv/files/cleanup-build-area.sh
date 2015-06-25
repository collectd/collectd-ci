#!/bin/sh

set -xe

test "x$(id -un)" = "xjenkins"

git config --local core.sparsecheckout true
git config --local user.email "ci@collectd.org"
git config --local user.name "Jenkins"

git reset --hard
rm -f env-*.sh env.sh

./clean.sh
