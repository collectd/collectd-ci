yum -y install sudo rsync

yum -y install epel-release
yum -y update

yum -y install autoconf automake bison byacc clang cpp curl flex gcc gdb git glibc-devel libgcrypt-devel libtool libtool-ltdl-devel m4 make nc pkgconfig strace tar valgrind

yum -y install OpenIPMI-devel ganglia-devel iproute-devel iptables-devel java-1.7.0-openjdk-devel java-devel jpackage-utils libatasmart-devel libcurl-devel libdbi-devel libesmtp-devel libmemcached-devel libmnl-devel libmodbus-devel libnotify-devel liboping-devel libpcap-devel librabbitmq-devel libstatgrab-devel libvirt-devel libxml2-devel lm_sensors-devel lvm2-devel mysql-devel net-snmp-devel nut-devel openldap-devel perl-ExtUtils-Embed postgresql-devel protobuf-c-devel python-devel rrdtool-devel varnish-libs-devel yajl-devel hiredis-devel libudev-devel

yum -y clean all
