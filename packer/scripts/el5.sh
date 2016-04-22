echo "net.ipv6.conf.all.disable_ipv6 = 1" >> /etc/sysctl.conf
sysctl -p

if [ "x$(uname -m)" = "xx86_64" ]; then
  rpm -qa --queryformat '%{NAME}.%{ARCH}\n' | grep 'i.86' | xargs rpm -e
  echo "exclude = *.i?86" >> /etc/yum.conf
fi

yum -y install sudo rsync

yum -y install epel-release
yum -y update

yum -y install autoconf automake bison cpp curl flex gcc gdb git glibc-devel libgcrypt-devel libtool libtool-ltdl-devel m4 make nc pkgconfig redhat-rpm-config strace tar valgrind

yum -y install OpenIPMI-devel curl-devel ganglia-devel gtk2-devel iptables-devel java-1.7.0-openjdk-devel java-devel jpackage-utils libcap-devel libdbi-devel libesmtp-devel libmemcached-devel libmnl-devel libnotify-devel liboping-devel libpcap-devel librabbitmq-devel libvirt-devel libxml2-devel lm_sensors-devel mysql-devel net-snmp-devel nut-devel openldap-devel postgresql-devel protobuf-c-devel python26-devel rrdtool-devel varnish-libs-devel openldap-devel xfsprogs-devel

yum -y clean all

mkdir -p /opt/jenkins
ln -s /usr/lib/jvm/jre-1.7.0/bin/java /opt/jenkins/
