test -n "$1"
TOKEN="$1"

BASEURL="https://scan.coverity.com/download"

if [ "x$(uname -m)" = "xamd64" -o "x$(uname -m)" = "xx86_64" ]; then
  ARCH_SUFFIX='64'
else
  ARCH_SUFFIX='32'
fi

case "$(uname)" in
  Linux)
    SUDO=""
    URL="$BASEURL/linux-${ARCH_SUFFIX}"
    ;;
  FreeBSD)
    SUDO="sudo"
    URL="$BASEURL/freeBSD-${ARCH_SUFFIX}"
    ;;
  *)
    echo "unsupported"
    exit 1
esac

curl -qo /tmp/coverity_tool.tgz "${URL}" -d "token=${TOKEN}&project=collectd%2Fcollectd"
curl -qo /tmp/coverity_tool.md5 "${URL}" -d "token=${TOKEN}&project=collectd%2Fcollectd&md5=1"

if [ "x$(uname)" = "xLinux" ]; then
  echo "$(cat /tmp/coverity_tool.md5) /tmp/coverity_tool.tgz" | md5sum -c - || exit 1
else
  md5 -c "$(cat /tmp/coverity_tool.md5)" /tmp/coverity_tool.tgz || exit 1
fi

$SUDO mkdir -p /opt
$SUDO tar -C /opt -xzvf /tmp/coverity_tool.tgz
$SUDO ln -s /opt/cov-analysis* /opt/coverity-scan

$SUDO tee /opt/coverity-scan/bin/coverity-submit << EOF
#!/bin/sh -e

test -n "\$1"
test -n "\$2"

curl --form token="${TOKEN}" \
  --form email=ci@collectd.org \
  --form file=@\$1 \
  --form version="\$2" \
  --form description="\$BUILD_TAG" \
  https://scan.coverity.com/builds?project=collectd%2Fcollectd

EOF

$SUDO chmod 0755 /opt/coverity-scan/bin/coverity-submit
