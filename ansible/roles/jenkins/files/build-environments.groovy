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
cat collectd-${COLLECTD_BUILD}/src/config.h || exit 0
ldd collectd-${COLLECTD_BUILD}/src/.libs/*.so || exit 0
'''

buildEnvironments = [
  jessie: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "${defaultTeardownTask}",
      ],
      [
        archs: ['i386', 'amd64'],
        buildName: 'clang',
        buildDescription: 'CC=clang',
        buildCommand: "./configure CC=clang && make V=1",
      ],
      [
        archs: ['amd64'],
        buildName: 'scan-build',
        buildDescription: 'clang\'s scan-build static analyzer',
        buildCommand: 'scan-build -o ./scan-build ./configure && scan-build -o ./scan-build make V=1',
        artifacts: '${WORKSPACE}/collectd-${COLLECTD_BUILD}/scan-build/*',
      ],
    ],
  ],

  wheezy: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "${defaultTeardownTask}",
      ],
    ],
  ],

  squeeze: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "${defaultTeardownTask}",
      ],
    ],
  ],

  trusty: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "${defaultTeardownTask}",
      ],
    ],
  ],

  precise: [
    buildJobs: [
      [
        archs: ['i386', 'amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.debian} && make V=1",
        setupTask: "${defaultSetupTask.debian}",
        teardownTask: "${defaultTeardownTask}",
      ],
    ],
  ],

  epel7: [
    buildJobs: [
      [
        archs: ['x86_64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.redhat} && make V=1",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "${defaultTeardownTask}",
      ],
    ],
  ],

  epel6: [
    buildJobs: [
      [
        archs: ['i386', 'x86_64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.redhat} && make V=1",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "${defaultTeardownTask}",
      ],
    ],
  ],

  epel5: [
    buildJobs: [
      [
        archs: ['i386', 'x86_64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.redhat} && make V=1",
        setupTask: "${defaultSetupTask.redhat}",
        teardownTask: "${defaultTeardownTask}",
      ],
    ],
  ],

  freebsd10: [
    buildJobs: [
      [
        archs: ['amd64'],
        buildName: 'default-toolchain',
        buildDescription: 'distro\'s default toolchain',
        buildCommand: "./configure ${defaultConfigureOpts.freebsd} && make V=1",
        setupTask: "${defaultSetupTask.freebsd}",
        teardownTask: "${defaultTeardownTask}",
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
    def jobName = "build-on-${distro}-with-${buildName}"

    def archList = []
    it.archs.each {
      archList << "${distro}-${it}"
    }

    // The following parameters are passed down from upstream to each of the
    // jobs: PR, COLLECTD_BUILD, TARBALL, TARBALL_BUILD_NUMBER
    matrixJob(jobName) {
      displayName("build on ${distro} (${buildDescription})")
      description('Configuration automatically generated, do not edit !')

      axes {
        label ('buildenv', archList)
      }

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

echo "### About to build ${COLLECTD_BUILD} from https://github.com/collectd/collectd/pull/${PR} on ${buildenv} ###"

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
