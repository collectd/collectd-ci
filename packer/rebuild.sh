#!/bin/bash

if [ $# -ne 1 ]; then
  echo "Usage: $0 <distro>"
  exit 1
fi

if [ "x" = "x$DIGITALOCEAN_API_TOKEN" ]; then
  echo "Missing \$DIGITALOCEAN_API_TOKEN environment variable"
  exit 1
fi

DISTRO=$1
TEMPLATE=$(basename "${DISTRO}.json")

if ! [ -f $TEMPLATE ]; then
  echo "File not found: ${TEMPLATE}"
  exit 1
fi

packer validate $TEMPLATE || exit 1

if $(grep -q digitalocean $TEMPLATE); then
  python ./delete_digitalocean_image.py "${DISTRO}"
fi

packer build $TEMPLATE
