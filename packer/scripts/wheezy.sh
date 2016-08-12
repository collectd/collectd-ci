apt-get -y install sudo rsync

sed -i 's/main$/main contrib non-free/' /etc/apt/sources.list
echo 'APT::Install-Recommends "0";' > /etc/apt/apt.conf.d/50no-install-recommends
echo 'APT::Install-Suggests "0";' > /etc/apt/apt.conf.d/50no-install-suggests

echo "net.ipv6.conf.all.disable_ipv6 = 1" >> /etc/sysctl.conf
sysctl -p

apt-get -y update
apt-get -y upgrade

apt-get -y install \
  autoconf \
  automake \
  bison \
  clang \
  cpp \
  dpkg-dev \
  flex \
  gcc \
  gcc-4.4 \
  gcc-4.6 \
  g++ \
  g++-4.4 \
  g++-4.6 \
  gdb \
  git \
  libc6-dev \
  libtool \
  m4 \
  make \
  pkg-config \
  strace \
  valgrind

apt-get -y install \
  autotools-dev \
  default-jdk \
  iproute-dev \
  iptables-dev \
  javahelper \
  libatasmart-dev \
  libcap-dev \
  libcurl4-gnutls-dev \
  libdbi0-dev \
  libesmtp-dev \
  libganglia1-dev \
  libgcrypt11-dev \
  libglib2.0-dev \
  libgps-dev \
  libhal-dev \
  libhiredis-dev \
  libi2c-dev \
  libldap2-dev \
  libltdl-dev \
  liblua50-dev \
  liblua5.1-0-dev \
  liblua5.2-dev \
  liblvm2-dev \
  libmemcached-dev \
  libmnl-dev \
  libmodbus-dev \
  libmosquitto0-dev \
  libmysqlclient-dev \
  libnotify-dev \
  libopenipmi-dev \
  liboping-dev \
  libow-dev \
  libpcap-dev \
  libperl-dev \
  libpq-dev \
  libprotobuf-c0-dev \
  librabbitmq-dev \
  librrd-dev \
  libsensors4-dev \
  libsnmp-dev \
  libstatgrab-dev \
  libtokyocabinet-dev \
  libtokyotyrant-dev \
  libudev-dev \
  libupsclient1-dev \
  libvarnish-dev \
  libvirt-dev \
  libxen-dev \
  libxml2-dev \
  libyajl-dev \
  linux-libc-dev \
  openjdk-7-jre-headless \
  perl \
  protobuf-c-compiler \
  python-dev \
  xfslibs-dev

apt-get -y clean

mkdir -p /opt/jenkins
ln -s /usr/lib/jvm/java-7-openjdk-$(dpkg --print-architecture)/bin/java /opt/jenkins/
