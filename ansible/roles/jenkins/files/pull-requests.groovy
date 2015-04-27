job('make-pr-tarball') {
  displayName('prepare tarball for pull-request testing')
  description('Configuration automatically generated, do not edit !')
  label('master')

  scm {
    git {
      remote {
        name('origin')
        url('https://github.com/collectd/collectd.git')
        refspec('+refs/pull/*/head:refs/remotes/origin/pr/*')
      }
      branch('*/pr/${pr}')
      mergeOptions('origin', 'master')
    }
  }

  steps {
    shell('''\
./clean.sh
./build.sh
./configure
make dist-gzip

COLLECTD_BUILD="$(./version-gen.sh)"
TARBALL="collectd-$COLLECTD_BUILD.tar.gz"
test -f "$TARBALL"
test -n "$BUILD_NUMBER"

cat << EOF > env-${PR}.sh
COLLECTD_BUILD=$COLLECTD_BUILD
TARBALL=$TARBALL
TARBALL_BUILD_NUMBER=$BUILD_NUMBER
EOF
''')
  }

  publishers {
    archiveArtifacts {
      pattern('collectd*.tar.gz')
    }
  }
}

multiJob('test-pull-requests') {
  displayName('test github pull-requests on various environements')
  description('Configuration automatically generated, do not edit !')
  label('master')

 concurrentBuild(false)

  parameters {
    stringParam('PR', null, 'Pull request number')
  }

  downstreamProperties = [
    COLLECTD_BUILD:       '$COLLECTD_BUILD',
    TARBALL:              '$TARBALL',
    TARBALL_BUILD_NUMBER: '$TARBALL_BUILD_NUMBER',
  ]

  steps {
    phase('prepare release tarball', 'SUCCESSFUL') {
      job('make-pr-tarball')
    }

    environmentVariables {
      propertiesFile('/var/lib/jenkins/jobs/make-pr-tarball/workspace/env-${PR}.sh')
    }

    phase('touchstone platforms', 'SUCCESSFUL') {
      job('build-on-jessie-with-default-toolchain') {
        currentJobParameters(true)
        props(downstreamProperties)
      }
    }

    phase('supported platforms', 'SUCCESSFUL') {
      job('build-on-trusty-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-precise-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-wheezy-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-squeeze-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-epel7-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-epel6-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-epel5-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-freebsd10-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
    }
  }

  publishers {
    downstreamParameterized {
      trigger('build-on-jessie-with-clang') {
        currentBuild()
        predefinedProps(downstreamProperties)
      }
      trigger('build-on-jessie-with-scan-build') {
        currentBuild()
        predefinedProps(downstreamProperties)
      }
    }
  }
}
