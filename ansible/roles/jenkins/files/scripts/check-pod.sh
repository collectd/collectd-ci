#!/bin/sh

set -xe

for podfile in $(find . -name '*.pod'); do podchecker -nowarnings $podfile || exit 1; done
