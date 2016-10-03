sudo pkg update -f

sudo pkg install -y \
  autoconf \
  automake \
  bison \
  curl \
  gdb \
  git \
  libtool \
  m4 \
  pkgconf \
  valgrind

sudo pkg install -y \
  ganglia-monitor-core \
  glib \
  gnutls \
  hiredis \
  libdbi \
  libesmtp \
  libgcrypt \
  libltdl \
  libmemcached \
  libmicrohttpd \
  libmodbus \
  libnotify \
  liboping \
  libpcap \
  librdkafka \
  librouteros \
  libsigrok \
  libstatgrab \
  libvirt \
  libxml2 \
  lua51 \
  lua52 \
  lua53 \
  mosquitto \
  mysql56-client \
  net-snmp \
  nut \
  openipmi \
  openjdk8 \
  openldap-sasl-client \
  owfs \
  postgresql94-client \
  protobuf \
  protobuf-c \
  python34 \
  python35 \
  rabbitmq-c-devel \
  rrdtool \
  tokyocabinet \
  tokyotyrant \
  varnish4 \
  xmms \
  yajl

sed '/ForceCommand.*droplet/d' /etc/ssh/sshd_config > ~/sshd_config
sudo cp ~/sshd_config /etc/ssh/sshd_config
sudo cp -a ~/.ssh /root/
sudo chown -R root:wheel /root/.ssh/

# required by openjdk
cat << EOF | sudo tee -a /etc/fstab
fdesc   /dev/fd         fdescfs         rw      0       0
proc    /proc           procfs          rw      0       0
EOF

sudo mkdir -p /opt/jenkins
sudo ln -s /usr/local/bin/java /opt/jenkins/
