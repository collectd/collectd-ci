echo "net.ipv6.conf.all.disable_ipv6 = 1" >> /etc/sysctl.conf
sysctl -p

dnf -y install sudo rsync

dnf -y update

dnf -y install \
  autoconf \
  automake \
  bison \
  clang \
  cpp \
  curl \
  flex \
  gcc \
  gcc-c++ \
  gdb \
  git \
  glibc-devel \
  libgcrypt-devel \
  libtool \
  libtool-ltdl-devel \
  m4 \
  make \
  nc \
  pkgconfig \
  redhat-rpm-config \
  strace \
  tar \
  valgrind

dnf -y install \
  ganglia-devel \
  gpsd-devel \
  gtk2-devel \
  hiredis-devel \
  iproute-devel \
  iptables-devel \
  java-1.8.0-openjdk-devel \
  java-devel \
  jpackage-utils \
  libatasmart-devel \
  libcap-devel \
  libcurl-devel \
  libdbi-devel \
  libesmtp-devel \
  libmemcached-devel \
  libmicrohttpd-devel \
  libmnl-devel \
  libmodbus-devel \
  libnotify-devel \
  liboping-devel \
  libpcap-devel \
  librabbitmq-devel \
  libsigrok-devel \
  libudev-devel \
  libvirt-devel \
  libxml2-devel \
  lm_sensors-devel \
  lua-devel \
  lvm2-devel \
  mosquitto-devel \
  mysql-devel \
  net-snmp-devel \
  nut-devel \
  OpenIPMI-devel \
  openldap-devel \
  owfs-devel \
  perl-ExtUtils-Embed \
  postgresql-devel \
  protobuf-c-devel \
  python-devel \
  python3-devel \
  riemann-c-client-devel \
  rrdtool-devel \
  varnish-libs-devel \
  xen-devel \
  xfsprogs-devel \
  xmms-devel \
  yajl-devel

dnf -y clean all

mkdir -p /opt/jenkins
ln -s /usr/lib/jvm/jre-1.8.0/bin/java /opt/jenkins/
