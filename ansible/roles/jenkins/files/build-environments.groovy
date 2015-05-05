def defaultConfigureOpts = [
  debian: [ // default build flags & options use when building .deb packages
    'CFLAGS="$(dpkg-buildflags --get CFLAGS) -Wall -Wno-error=deprecated-declarations"',
    'CPPLAGS="$(dpkg-buildflags --get CPPFLAGS) -DLT_LAZY_OR_NOW=\'RTLD_LAZY|RTLD_GLOBAL\'"',
    'LDFLAGS="$(dpkg-buildflags --get LDFLAGS)"',
    'JAVAC="/usr/lib/jvm/default-java/bin/javac"',
    'JAR="/usr/lib/jvm/default-java/bin/jar"',
    'JAVA_CPPFLAGS="/usr/lib/jvm/default-java/include"',
    'JAVA_LDFLAGS="-L/usr/lib/jvm/default-java/jre/lib/$(/usr/share/javahelper/java-arch.sh)/server -Wl,-rpath -Wl,/usr/lib/jvm/default-java/jre/lib/$(/usr/share/javahelper/java-arch.sh)/server"',
    '--enable-debug',
  ].join(' '),

  redhat: [ // default build flags & options use when building .rpm packages
    'CFLAGS="$(rpm --eval \'%optflags\')"',
    'CPPFLAGS="-DLT_LAZY_OR_NOW=\'RTLD_LAZY|RTLD_GLOBAL\'"',
    'JAVAC="$(rpm --eval \'%java_home\')/bin/javac"',
    'JAR="$(rpm --eval \'%java_home\')/bin/jar"',
    '--with-java="$(rpm --eval \'%java_home\')"',
    '--enable-debug',
  ].join(' '),

  freebsd: [
    '--with-libyajl=/usr/local/',
    '--with-libhiredis=/usr/local/',
    '--with-libdbi=/usr/local',
    '--with-librabbitmq=/usr/local',
    '--with-libesmtp=/usr/local',
    '--with-libldap=/usr/local',
    '--with-libmemcached=/usr/local',
    '--with-libmodbus=/usr/local',
    '--with-liboping=/usr/local',
    '--with-librrd=/usr/local',
    '--with-python=/usr/local/bin/python3.4',
    '--with-java=/usr/local/openjdk8',
    '--enable-debug',
    '--disable-modbus --disable-routeros',
   ].join(' '),
]

def defaultSetupTask = [
  debian: '''
echo "installed packages:"
dpkg -l
''',
  redhat: '''
echo "installed packages:"
rpm -qa | sort
''',
  freebsd: '''
echo "installed packages:"
pkg query %n-%v
''',
]

def defaultTeardownTask = '''
set +x

SRCDIR="collectd-${COLLECTD_BUILD}/src"

echo "### Generated src/config.h: ###"
cat ${SRCDIR}/config.h

echo "### Checking whether all known working plugins on this platform have been built ###"
STATUS=0
for i in ${PLUGIN_LIST}; do
  echo "$i plugin:"
  if test -f ${SRCDIR}/$i.c; then
    ldd "${SRCDIR}/.libs/${i}.so" || STATUS=1
  else
    echo "... doesn't exist in this version"
  fi
done

echo "### Checking for new/unknown plugins on this platform ###"
for i in ${SRCDIR}/.libs/*.so; do
  plugin="$(basename $i)"
  FOUND=0
  for j in ${PLUGIN_LIST}; do
    [ "x${plugin}" = "x${j}.so" ] && FOUND=1
  done
  if [ $FOUND -eq 0 ]; then
    echo "found unknown plugin: ${plugin}"
    ldd "${SRCDIR}/.libs/${plugin}"
  fi
done

exit $STATUS
'''

def pluginList = [ // list of plugins known to build on each platform
  jessie: 'aggregation amqp apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec filecount fscache gmond hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus multimeter mysql netlink network nfs nginx notify_desktop notify_email ntpd numa nut olsrd onewire openldap openvpn perl perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial sigrok smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_kafka write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  wheezy: 'aggregation amqp apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec filecount fscache gmond hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus multimeter mysql netlink network nfs nginx notify_desktop notify_email ntpd numa nut olsrd onewire openldap openvpn perl perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  squeeze: 'aggregation apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec filecount fscache gmond hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory multimeter mysql network nfs nginx notify_desktop notify_email ntpd numa nut olsrd openldap openvpn perl perl pinba ping postgresql powerdns processes protocols python rrdcached rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  trusty: 'aggregation amqp apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec filecount fscache gmond hddtemp interface ipc iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus multimeter mysql netlink network nfs nginx notify_desktop notify_email ntpd numa nut olsrd onewire openldap openvpn perl perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial sigrok smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_kafka write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  precise: 'aggregation amqp apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec filecount fscache gmond hddtemp interface ipc iptables ipvs irq java load logfile log_logstash madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus multimeter mysql netlink network nfs nginx notify_desktop notify_email ntpd numa nut olsrd onewire openldap openvpn perl perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  epel7: 'aggregation amqp apache apcups ascent battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec filecount fscache hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcached memory multimeter mysql netlink network nfs nginx notify_desktop notify_email ntpd numa nut olsrd openldap openvpn perl perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  epel6: 'aggregation amqp apache apcups ascent battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec filecount fscache gmond hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus multimeter mysql netlink network nfs nginx notify_desktop notify_email ntpd numa nut olsrd openldap openvpn perl perl pinba ping postgresql powerdns processes protocols python redis rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  epel5: 'aggregation amqp apache apcups ascent battery bind cgroups conntrack contextswitch cpu cpufreq csv curl curl_xml dbi df disk drbd email entropy exec filecount fscache hddtemp interface ipc ipmi irq java load logfile madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory multimeter mysql netlink network nfs nginx notify_desktop notify_email ntpd numa nut olsrd openldap openvpn perl perl pinba ping postgresql powerdns processes protocols rrdtool sensors serial snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  freebsd10: 'aggregation amqp apache apcups ascent bind ceph contextswitch cpu csv curl curl_json curl_xml dbi df disk dns email exec filecount hddtemp interface ipmi load logfile log_logstash match_empty_counter match_hashed match_regex match_timediff match_value mbmon memcachec memcached memory multimeter mysql network nginx notify_desktop notify_email ntpd nut olsrd openldap openvpn perl perl pf ping postgresql powerdns processes python redis rrdcached rrdtool sigrok snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted threshold tokyotyrant unixsock uptime users uuid varnish virt write_graphite write_http write_log write_mongodb write_redis write_sensu write_tsdb zfs_arc zookeeper',
]

buildEnvironments = [
  jessie: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain and dpkg build options',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1 && make check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.jessie}\"; ${defaultTeardownTask}",
      ],
      [
        archs: ['i386', 'amd64'],
        buildName: 'clang',
        buildDescription: 'CC="clang -Wall -Werror"',
        buildCommand: './configure --enable-debug CC="clang -Wall -Werror" && make V=1 && make check',
        teardownTask: "PLUGIN_LIST=\"${pluginList.jessie}\"; ${defaultTeardownTask}",
      ],
      [
        archs: ['amd64'],
        buildName: 'clang-strict',
        buildDescription: 'CC="clang -Wall -Werror -Wextra -Wpedantic -Wconversion -Wformat=2 -Wshadow -Wunreachable-code"',
        buildCommand: './configure --enable-debug && make CC="clang -Wall -Werror -Wextra -Wpedantic -Wconversion -Wformat=2 -Wshadow -Wunreachable-code" V=1 && make check',
        teardownTask: "PLUGIN_LIST=\"${pluginList.jessie}\"; ${defaultTeardownTask}",
      ],
      [
        archs: ['amd64'],
        buildName: 'scan-build',
        buildDescription: 'clang\'s scan-build static analyzer',
        buildCommand: 'scan-build -o ./scan-build ./configure --enable-debug && scan-build -o ./scan-build make V=1',
        artifacts: 'collectd-${COLLECTD_BUILD}/scan-build/**',
      ],
    ],
  ],

  wheezy: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain and dpkg build options',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1 && make check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.wheezy}\"; ${defaultTeardownTask}",
      ],
    ],
  ],

  squeeze: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain and dpkg build options',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1 && make check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.squeeze}\"; ${defaultTeardownTask}",
      ],
    ],
  ],

  trusty: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain and dpkg build options',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1 && make check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.trusty}\"; ${defaultTeardownTask}",
      ],
    ],
  ],

  precise: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain and dpkg build options',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1 && make check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.precise}\"; ${defaultTeardownTask}",
      ],
    ],
  ],

  epel7: [
    buildJobs: [
      [
        archs: ['x86_64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain and rpm build options',
        buildCommand: "./configure ${defaultConfigureOpts.redhat} && make V=1 && make check",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.epel7}\"; ${defaultTeardownTask}",
      ],
    ],
  ],

  epel6: [
    buildJobs: [
      [
        archs: ['i386', 'x86_64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain and rpm build options',
        buildCommand: "./configure ${defaultConfigureOpts.redhat} && make V=1 && make check",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.epel6}\"; ${defaultTeardownTask}",
      ],
    ],
  ],

  epel5: [
    buildJobs: [
      [
        archs: ['i386', 'x86_64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain and rpm build options',
        buildCommand: "./configure ${defaultConfigureOpts.redhat} && make V=1 && make check",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.epel5}\"; ${defaultTeardownTask}",
      ],
    ],
  ],

  freebsd10: [
    buildJobs: [
      [
        archs: ['amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.freebsd} && make V=1 && make check",
        setupTask: "${defaultSetupTask.freebsd}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.freebsd10}\"; ${defaultTeardownTask}",
      ],
    ],
  ],
]

buildEnvironments.each { distro, options ->

  options.buildJobs.each {
    def buildName = it?.buildName
    def buildDescription = it?.buildDescription
    def buildCommand = it?.buildCommand
    def setupTask = it?.setupTask
    def teardownTask = it?.teardownTask
    def artifacts = it?.artifacts

    it.archs.each {
      def arch = "${it}"
      def jobName = "build-on-${distro}-${arch}-with-${buildName}"

      // The following parameters are passed down from upstream to each of the
      // jobs: PR, COLLECTD_BUILD, TARBALL, TARBALL_BUILD_NUMBER
      job(jobName) {
        displayName("build on ${distro}-${arch} (${buildDescription})")
        description("""
This job builds the tarball passed down from the 'make-pr-tarball' job on the '${distro}-${arch}' platform, with various build parameters and optional setup/teardown tasks.

Configuration generated automatically, do not edit!
""")
        label("${distro}-${arch}")

        steps {
          if (setupTask != null) {
            shell(setupTask)
          }

          copyArtifacts('make-pr-tarball', '$TARBALL') {
            buildNumber('$TARBALL_BUILD_NUMBER')
          }

          shell('''\
test -f "$TARBALL"
test -n "$COLLECTD_BUILD"
test -n "$PR"

echo "### About to build ${COLLECTD_BUILD} from https://github.com/collectd/collectd/pull/${PR} ###"

test -f /usr/bin/apt-get && apt-get -y update && apt-get -y install libcap-dev
test -f /usr/bin/yum && yum -y install libcap-devel

tar -xzvf $TARBALL
cd collectd-${COLLECTD_BUILD}
''' + "${buildCommand}")

          if (teardownTask != null) {
            shell(teardownTask)
          }
        }

        publishers {
          if (artifacts != null) {
            archiveArtifacts {
              pattern(artifacts)
            }
          }
        }
      }
    }
  }
}
