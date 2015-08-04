#!/bin/sh

set -xe

for shellscript in *.sh; do checkbashisms -n $shellscript || exit 1; done
