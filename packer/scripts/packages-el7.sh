set -e

yum -y install sudo rsync

yum -y install epel-release
yum -y update

yum -y install autoconf automake bison byacc cpp curl flex gcc gdb git glibc-devel libgcrypt-devel libtool libtool-ltdl-devel m4 make nc pkgconfig strace tar valgrind

yum -y install OpenIPMI-devel gtk2-devel iproute-devel iptables-devel java-1.7.0-openjdk-devel java-devel jpackage-utils libatasmart-devel libcurl-devel libdbi-devel libesmtp-devel hiredis-devel libmemcached-devel libmnl-devel libnotify-devel openldap-devel liboping-devel libpcap-devel librabbitmq-devel libudev-devel libvirt-devel libxml2-devel lm_sensors-devel lvm2-devel mysql-devel net-snmp-devel nut-devel openldap-devel perl-ExtUtils-Embed postgresql-devel protobuf-c-devel python-devel rrdtool-devel varnish-libs-devel yajl-devel

yum -y clean all
