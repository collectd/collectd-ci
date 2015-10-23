apt-get -y install sudo rsync

sed -i 's/main$/main contrib non-free/' /etc/apt/sources.list
echo 'APT::Install-Recommends "0";' > /etc/apt/apt.conf.d/50no-install-recommends
echo 'APT::Install-Suggests "0";' > /etc/apt/apt.conf.d/50no-install-suggests

echo "net.ipv6.conf.all.disable_ipv6 = 1" >> /etc/sysctl.conf
sysctl -p

apt-get -y update
apt-get -y upgrade

apt-get -y install autoconf automake bison cpp dpkg-dev flex gcc gcc-4.1 gcc-4.3 gdb git libc6-dev libtool m4 make pkg-config strace valgrind
apt-get -y install autotools-dev default-jdk iproute-dev iptables-dev javahelper libapr1-dev libatasmart-dev libcap-dev libconfuse-dev libcurl4-gnutls-dev libdbi0-dev libesmtp-dev libganglia1-dev libgcrypt11-dev libglib2.0-dev libhal-dev libi2c-dev libldap2-dev libltdl-dev liblvm2-dev libmemcached-dev libmysqlclient-dev libnotify-dev libopenipmi-dev liboping-dev libpcap-dev libperl-dev libpq-dev libprotobuf-c0-dev librrd-dev libsensors4-dev libsnmp-dev libstatgrab-dev libtokyocabinet-dev libtokyotyrant-dev libudev-dev libupsclient1-dev libvarnish-dev libvirt-dev libxml2-dev libyajl-dev linux-libc-dev perl protobuf-c-compiler python-dev

# install java 7, required to run jenkins slave agent
echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu precise main" | tee -a /etc/apt/sources.list
apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886
apt-get -y update
cat << EOF > /tmp/oracle-java7-installer.preseed
oracle-java7-installer  shared/present-oracle-license-v1-1      note
oracle-java7-installer  shared/accepted-oracle-license-v1-1     boolean true
oracle-java7-installer  oracle-java7-installer/local    string
EOF
debconf-set-selections /tmp/oracle-java7-installer.preseed
apt-get -y install oracle-java7-installer
update-java-alternatives --set java-1.6.0-openjdk

apt-get -y clean

mkdir -p /opt/jenkins
ln -s /usr/lib/jvm/java-7-oracle/jre/bin/java /opt/jenkins/
