
sed -i 's/main$/main contrib non-free/' /etc/apt/sources.list
echo 'APT::Install-Recommends "0";' > /etc/apt/apt.conf.d/50no-install-recommends
echo 'APT::Install-Suggests "0";' > /etc/apt/apt.conf.d/50no-install-suggests

echo "net.ipv6.conf.all.disable_ipv6 = 1" >> /etc/sysctl.conf
sysctl -p

apt-get -y update
apt-get -y upgrade

apt-get -y install sudo rsync curl

apt-get -y install \
  autoconf \
  automake \
  bison \
  clang \
  cpp \
  dpkg-dev \
  flex \
  gcc \
  g++ \
  gdb \
  git \
  libc6-dev \
  libtool \
  m4 \
  make \
  musl-dev \
  musl-tools \
  pkg-config \
  strace \
  valgrind

apt-get -y install \
  autotools-dev \
  default-jdk \
  iptables-dev \
  javahelper \
  libatasmart-dev \
  libcap-dev \
  libcurl4-gnutls-dev \
  libdbi-dev \
  libdpdk-dev \
  libesmtp-dev \
  libganglia1-dev \
  libgcrypt20-dev \
  libglib2.0-dev \
  libgps-dev \
  libhiredis-dev \
  libi2c-dev \
  libldap2-dev \
  libltdl-dev \
  liblua50-dev \
  liblua5.1-0-dev \
  liblua5.2-dev \
  liblvm2-dev \
  libmemcached-dev \
  libmicrohttpd-dev \
  libmnl-dev \
  libmodbus-dev \
  libmongoc-dev \
  libmosquitto-dev \
  default-libmysqlclient-dev \
  libnotify-dev \
  libopenipmi-dev \
  liboping-dev \
  libow-dev \
  libpcap-dev \
  libperl-dev \
  libpq-dev \
  libprotobuf-c-dev \
  libqpid-proton8-dev \
  librabbitmq-dev \
  librdkafka-dev \
  libriemann-client-dev \
  librrd-dev \
  libsensors4-dev \
  libsigrok-dev \
  libsnmp-dev \
  libssl-dev \
  libstatgrab-dev \
  libtokyocabinet-dev \
  libtokyotyrant-dev \
  libudev-dev \
  libupsclient-dev \
  libvarnishapi-dev \
  libvirt-dev \
  libxen-dev \
  libxml2-dev \
  libyajl-dev \
  linux-libc-dev \
  perl \
  protobuf-c-compiler \
  protobuf-compiler \
  python-dev \
  python3-dev \
  xfslibs-dev

apt-get -y clean

mkdir -p /opt/jenkins
ln -s /usr/lib/jvm/java-8-openjdk-$(dpkg --print-architecture)/bin/java /opt/jenkins/
