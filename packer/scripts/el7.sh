echo "net.ipv6.conf.all.disable_ipv6 = 1" >> /etc/sysctl.conf
sysctl -p

yum -y install sudo rsync

yum -y install epel-release
yum -y update

yum -y install autoconf automake bison clang cpp curl flex gcc gdb git glibc-devel libgcrypt-devel libtool libtool-ltdl-devel m4 make nc pkgconfig redhat-rpm-config strace tar valgrind

yum -y install OpenIPMI-devel ganglia-devel gtk2-devel gpsd-devel iproute-devel iptables-devel java-1.7.0-openjdk-devel java-devel jpackage-utils libatasmart-devel libcap-devel libcurl-devel libdbi-devel libesmtp-devel hiredis-devel libmemcached-devel libmnl-devel mosquitto-devel libnotify-devel openldap-devel libmodbus-devel liboping-devel libpcap-devel librabbitmq-devel libudev-devel libvirt-devel libxml2-devel lm_sensors-devel lvm2-devel mysql-devel net-snmp-devel nut-devel openldap-devel perl-ExtUtils-Embed postgresql-devel protobuf-c-devel python-devel rrdtool-devel varnish-libs-devel xmms-devel yajl-devel

yum -y clean all

mkdir -p /opt/jenkins
ln -s /usr/lib/jvm/jre-1.7.0/bin/java /opt/jenkins/
