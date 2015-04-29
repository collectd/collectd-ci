job('make-pr-tarball') {
  displayName('prepare tarball for pull-request testing')
  description("""
This job:
 * merges the pull-request with the master branch
 * generates a release tarball and archives it
 * exports a couple of environment variables to allow downstream tasks to refer to the release tarball

Configuration generated automatically, do not edit!
""")
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
  description("""
This multi-step job aggregates various independent tasks, allowing to compute a global build status from them and report this back to github.

This job takes one parameter: a pull-request number from https://github.com/collectd/collectd/pulls

A couple of non-critical jobs are also defined as 'downstream jobs'. They are triggered only if all the others are successful and their exit status won't influence the global status.

Configuration generated automatically, do not edit!
""")
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

    // NB: unforunately "phase" blocks don't support groovy iterators, so this
    // forces us to file all the jobs manually here.
    phase('touchstone (won\'t continue further down if this step fails)', 'SUCCESSFUL') {
      job('build-on-jessie-amd64-with-default-toolchain') {
        currentJobParameters(true)
        props(downstreamProperties)
      }
    }

    phase('mandatory (platforms for which packages are built)', 'SUCCESSFUL') {
      job('build-on-jessie-i386-with-default-toolchain') {
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-trusty-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-trusty-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-precise-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-precise-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-wheezy-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-wheezy-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-squeeze-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-squeeze-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-epel7-x86_64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-epel6-x86_64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-epel6-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-epel5-x86_64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
      job('build-on-epel5-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
    }

    phase('supported (platforms known to work that new patches shouldn\'t break)', 'SUCCESSFUL') {
      job('build-on-freebsd10-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        currentJobParameters(true)
        props(downstreamProperties)
      }
    }
  }

  publishers {
    downstreamParameterized {
      trigger('build-on-jessie-amd64-with-clang') {
        currentBuild()
        predefinedProps(downstreamProperties)
      }
      trigger('build-on-jessie-i386-with-clang') {
        currentBuild()
        predefinedProps(downstreamProperties)
      }
      trigger('build-on-jessie-amd64-with-scan-build') {
        currentBuild()
        predefinedProps(downstreamProperties)
      }
    }
  }
}
