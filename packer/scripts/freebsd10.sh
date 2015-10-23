
sudo pkg install -y autoconf automake bison gdb git libtool m4 pkgconf valgrind

sudo pkg install -y openjdk8 gnutls libdbi libesmtp libgcrypt glib hiredis openldap-sasl-client libltdl libmemcached libmodbus mosquitto mysql56-client libnotify openipmi liboping libpcap postgresql94-client protobuf protobuf-c rabbitmq-c-devel rrdtool net-snmp libstatgrab tokyocabinet tokyotyrant nut varnish4 libvirt libxml2 yajl python34 libsigrok librouteros

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
