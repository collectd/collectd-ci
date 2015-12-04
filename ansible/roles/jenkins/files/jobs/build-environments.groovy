def defaultConfigureOpts = [
  common: [
    '--enable-debug',
    '--disable-dependency-tracking',
    '--without-libstatgrab',
  ].join(' '),

  debian: [ // default build flags & options used when building .deb packages
    'CFLAGS="$(dpkg-buildflags --get CFLAGS) -Wall -Wno-error=deprecated-declarations"',
    'CPPLAGS="$(dpkg-buildflags --get CPPFLAGS)"',
    'LDFLAGS="$(dpkg-buildflags --get LDFLAGS)"',
  ].join(' '),

  redhat: [ // default build flags & options used when building .rpm packages
    'CFLAGS="$(rpm --eval \'%optflags\')"',
  ].join(' '),

  freebsd: [
    '--with-libdbi=/usr/local',
    '--with-libesmtp=/usr/local',
    '--with-libhiredis=/usr/local/',
    '--with-libldap=/usr/local',
    '--with-libmemcached=/usr/local',
    '--with-libmosquitto=/usr/local',
    '--with-liboping=/usr/local',
    '--with-librabbitmq=/usr/local',
    '--with-librrd=/usr/local',
    '--with-libyajl=/usr/local/',
    '--with-python=/usr/local/bin/python3.4',
    '--with-java=/usr/local/openjdk8',
   ].join(' '),

   statgrab: [
     '--with-libstatgrab',
     '--disable-all-plugins',
     '--enable-cpu',
     '--enable-disk',
     '--enable-interface',
     '--enable-load',
     '--enable-memory',
     '--enable-swap',
     '--enable-users',
   ].join(' ')
]

def defaultSetupTask = [
  debian: '''
dpkg -l > dpkg-l.txt
''',
  redhat: '''
rpm -qa --nosignature --nodigest | sort > rpm-qa.txt
''',
  freebsd: '''
pkg query %n-%v > pkg-query.txt
''',
]

def defaultTeardownTask = '''
set +x

SRCDIR="collectd-${COLLECTD_BUILD}/src"

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

echo "### Looking for any plugins previously unsupported on this platform ###"
for i in ${SRCDIR}/.libs/*.so; do
  plugin="$(basename $i)"
  FOUND=0
  for j in ${PLUGIN_LIST}; do
    [ "x${plugin}" = "x${j}.so" ] && FOUND=1
  done
  if [ $FOUND -eq 0 ]; then
    echo "found this new plugin: ${plugin}"
    ldd "${SRCDIR}/.libs/${plugin}"
  fi
done

exit $STATUS
'''

def statgrabTeardownTask = '''
set +x

PLUGIN_LIST="cpu disk interface load memory swap users"
SRCDIR="collectd-${COLLECTD_BUILD}/src"
STATUS=0

for i in ${PLUGIN_LIST}; do
  if $(ldd "${SRCDIR}/.libs/${i}.so" | grep -q 'libstatgrab.so'); then
    echo "plugin $i linked against libstatgrab"
  else
    echo "plugin $i NOT linked against libstatgrab"
    STATUS=1
  fi
done

exit $STATUS
'''

def pluginList = [ // list of plugins known to build on each platform
  jessie: 'aggregation amqp apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec fhcount filecount fscache gmond hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus mqtt multimeter mysql netlink network nfs nginx notify_desktop notify_email notify_nagios ntpd numa nut olsrd onewire openldap openvpn perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial sigrok smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant turbostat unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_kafka write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  wheezy: 'aggregation amqp apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec fhcount filecount fscache gmond hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus mqtt multimeter mysql netlink network nfs nginx notify_desktop notify_email notify_nagios ntpd numa nut olsrd onewire openldap openvpn perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  squeeze: 'aggregation apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec fhcount filecount fscache gmond hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory multimeter mysql network nfs nginx notify_desktop notify_email notify_nagios ntpd numa nut olsrd openldap openvpn perl pinba ping postgresql powerdns processes protocols python rrdcached rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  trusty: 'aggregation amqp apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec fhcount filecount fscache gmond hddtemp interface ipc iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus mqtt multimeter mysql netlink network nfs nginx notify_desktop notify_email notify_nagios ntpd numa nut olsrd onewire openldap openvpn perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial sigrok smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant turbostat unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_kafka write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  precise: 'aggregation amqp apache apcups ascent barometer battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec fhcount filecount fscache gmond hddtemp interface ipc iptables ipvs irq java load logfile log_logstash madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus mqtt multimeter mysql netlink network nfs nginx notify_desktop notify_email notify_nagios ntpd numa nut olsrd onewire openldap openvpn perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold tokyotyrant unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  epel7: 'aggregation amqp apache apcups ascent battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec fhcount filecount fscache hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcached memory mqtt multimeter mysql netlink network nfs nginx notify_desktop notify_email notify_nagios ntpd numa nut olsrd openldap openvpn perl pinba ping postgresql powerdns processes protocols python redis rrdcached rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold turbostat unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  epel6: 'aggregation amqp apache apcups ascent battery bind ceph cgroups conntrack contextswitch cpu cpufreq csv curl curl_json curl_xml dbi df disk dns drbd email entropy ethstat exec fhcount filecount fscache gmond hddtemp interface ipc ipmi iptables ipvs irq java load logfile log_logstash lvm madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory modbus multimeter mysql netlink network nfs nginx notify_desktop notify_email notify_nagios ntpd numa nut olsrd openldap openvpn perl pinba ping postgresql powerdns processes protocols python redis rrdtool sensors serial smart snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold turbostat unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  epel5: 'aggregation amqp apache apcups ascent battery bind cgroups conntrack contextswitch cpu cpufreq csv curl curl_xml dbi df disk drbd email entropy exec fhcount filecount fscache gmond hddtemp interface ipc ipmi irq java load logfile madwifi match_empty_counter match_hashed match_regex match_timediff match_value mbmon md memcachec memcached memory multimeter mysql netlink network nfs nginx notify_desktop notify_email notify_nagios ntpd numa nut olsrd openldap openvpn perl pinba ping postgresql powerdns processes protocols rrdtool sensors serial snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted thermal threshold unixsock uptime users uuid varnish virt vmem vserver wireless write_graphite write_http write_log write_riemann write_sensu write_tsdb zfs_arc zookeeper',
  freebsd10: 'aggregation amqp apache apcups ascent bind ceph contextswitch cpu csv curl curl_json curl_xml dbi df disk dns email exec filecount gmond hddtemp interface ipmi load logfile log_logstash match_empty_counter match_hashed match_regex match_timediff match_value mbmon memcachec memcached memory modbus mqtt multimeter mysql network nginx notify_desktop notify_email notify_nagios ntpd nut olsrd openldap openvpn perl pf pinba ping postgresql powerdns processes python redis routeros rrdcached rrdtool sigrok snmp statsd swap syslog table tail tail_csv target_notification target_replace target_scale target_set target_v5upgrade tcpconns teamspeak2 ted threshold tokyotyrant unixsock uptime users uuid varnish virt write_graphite write_http write_log write_redis write_riemann write_sensu write_tsdb zfs_arc zookeeper',
]

buildEnvironments = [
  jessie: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: "distro's default toolchain and dpkg build options",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} && make -sk; make -sk check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.jessie}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
      [
        archs: ['amd64'],
        buildName: 'clang',
        buildDescription: 'CC="clang"',
        buildCommand: "./configure CC=clang ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} && make -sk; make -sk check",
        teardownTask: "PLUGIN_LIST=\"${pluginList.jessie}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['Clang (LLVM based)'],
      ],
      [
        archs: ['amd64'],
        buildName: 'clang-strict',
        buildDescription: 'CC="clang -Wall -Werror -Wextra -Wpedantic -Wconversion -Wformat=2 -Wshadow -Wunreachable-code"',
        buildCommand: "./configure ${defaultConfigureOpts.common} CC=clang CFLAGS='-Wall -Werror -Wextra -Wpedantic -Wconversion -Wformat=2 -Wshadow -Wunreachable-code' && make -sk || exit 0; make -sk check || exit 0",
        warning: ['Clang (LLVM based)'],
      ],
      [
        archs: ['amd64'],
        buildName: 'scan-build',
        buildDescription: "clang's scan-build static analyzer",
        buildCommand: "scan-build -k -o ./scan-build ./configure ${defaultConfigureOpts.common} && scan-build -k -o ./scan-build make",
        artifacts: ['collectd-${COLLECTD_BUILD}/scan-build/**'],
      ],
      [
        archs: ['amd64'],
        buildName: 'libstatgrab',
        buildDescription: "default toolchain, using libstatgrab",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} ${defaultConfigureOpts.statgrab} && make -sk",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "${statgrabTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
      [
        archs: ['amd64'],
        buildName: 'musl-libc',
        buildDescription: "CC=musl-gcc (wrapper for linking with musl-libc)",
        buildCommand: "./configure CC=musl-gcc ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} && make -sk || exit 0; make -sk check || exit 0",
        setupTask: "${defaultSetupTask.debian}",
        artifacts: ['collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
    ],
  ],

  wheezy: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: "distro's default toolchain and dpkg build options",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} && make -sk; make -sk check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.wheezy}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
      [
        archs: ['amd64'],
        buildName: 'libstatgrab',
        buildDescription: "default toolchain, using libstatgrab",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} ${defaultConfigureOpts.statgrab} && make -sk",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "${statgrabTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
    ],
  ],

  squeeze: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: "distro's default toolchain and dpkg build options",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} && make -sk; make -sk check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.squeeze}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
      [
        archs: ['amd64'],
        buildName: 'libstatgrab',
        buildDescription: "default toolchain, using libstatgrab",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} ${defaultConfigureOpts.statgrab} && make -sk",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "${statgrabTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
    ],
  ],

  trusty: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: "distro's default toolchain and dpkg build options",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} && make -sk; make -sk check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.trusty}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
    ],
  ],

  precise: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: "distro's default toolchain and dpkg build options",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.debian} && make -sk; make -sk check",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.precise}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'dpkg-l.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
    ],
  ],

  epel7: [
    buildJobs: [
      [
        archs: ['x86_64'],
        buildName: 'default-toolchain',
        buildDescription: "distro's default toolchain and rpm build options",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.redhat} && make -sk; make -sk check",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.epel7}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'rpm-qa.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
    ],
  ],

  epel6: [
    buildJobs: [
      [
        archs: ['i386', 'x86_64'],
        buildName: 'default-toolchain',
        buildDescription: "distro's default toolchain and rpm build options",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.redhat} && make -sk; make -sk check",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.epel6}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'rpm-qa.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
      [
        archs: ['x86_64'],
        buildName: 'libstatgrab',
        buildDescription: "default toolchain, using libstatgrab",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.redhat} ${defaultConfigureOpts.statgrab} && make -sk",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "${statgrabTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'rpm-qa.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
    ],
  ],

  epel5: [
    buildJobs: [
      [
        archs: ['i386', 'x86_64'],
        buildName: 'default-toolchain',
        buildDescription: "distro's default toolchain and rpm build options",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.redhat} && make -sk; make -sk check",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.epel5}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'rpm-qa.txt'],
        warning: ['GNU Make + GNU C Compiler (gcc)'],
      ],
    ],
  ],

  freebsd10: [
    buildJobs: [
      [
        archs: ['amd64'],
        buildName: 'default-toolchain',
        buildDescription: "distro's default toolchain",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.freebsd} && make -sk; make -sk check",
        setupTask: "${defaultSetupTask.freebsd}",
        teardownTask: "PLUGIN_LIST=\"${pluginList.freebsd10}\"; ${defaultTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/**/test_*.log', 'collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'pkg-query.txt'],
        warning: ['Clang (LLVM based)'],
      ],
      [
        archs: ['amd64'],
        buildName: 'libstatgrab',
        buildDescription: "default toolchain, using libstatgrab",
        buildCommand: "./configure ${defaultConfigureOpts.common} ${defaultConfigureOpts.freebsd} ${defaultConfigureOpts.statgrab} && make -sk",
        setupTask: "${defaultSetupTask.freebsd}",
        teardownTask: "${statgrabTeardownTask}",
        artifacts: ['collectd-${COLLECTD_BUILD}/src/config.h', 'collectd-${COLLECTD_BUILD}/config.log', 'pkg-query.txt'],
        warning: ['Clang (LLVM based)'],
      ],
    ],
  ],
]

['pull-requests', 'master'].each {
  def task = "${it}"

  buildEnvironments.each { distro, options ->

    options.buildJobs.each {
      def buildName = it?.buildName
      def buildDescription = it?.buildDescription
      def buildCommand = it?.buildCommand
      def setupTask = it?.setupTask
      def teardownTask = it?.teardownTask
      def artifacts = it?.artifacts
      def warning = it?.warning

      it.archs.each {
        def arch = "${it}"
        def jobName = "${task}-build-on-${distro}-${arch}-with-${buildName}"

        // The following parameters are passed down from upstream to each of the
        // jobs: COLLECTD_BUILD, TARBALL, TARBALL_BUILD_NUMBER
        job(jobName) {
          displayName("Build ${task} on ${distro}-${arch} (${buildDescription})")
          description("""
This job builds the tarball passed down from the '${task}-prepare-tarball' job on the '${distro}-${arch}' platform, with various build parameters and optional setup/teardown tasks.

Configuration generated automatically, do not edit!
""")
          label("${distro}-${arch}")

          steps {
            if (setupTask != null) {
              shell(setupTask)
            }

            copyArtifacts('$UPSTREAM_JOB_NAME') {
              includePatterns('$TARBALL')
              buildSelector {
                buildNumber('$TARBALL_BUILD_NUMBER')
              }
            }

            shell('''\
test -f "$TARBALL"
test -n "$COLLECTD_BUILD"

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
                artifacts.each {
                  pattern("${it}")
                }
              }
            }

            if (warning != null) {
              warnings (warning)
            }
          }
        }
      }
    }
  }
}
