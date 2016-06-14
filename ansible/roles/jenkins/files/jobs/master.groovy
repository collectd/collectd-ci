def githubOrg = 'collectd'
def setupGithubHooks = SETUP_GITHUB_HOOKS // this is the env. var defined in Job_DSL_seed_config.xml

multiJob('master-aggregation') {
  displayName("Aggregate master branch tests")
  description("""
This job will run various checks against the master branch:

 * merge bugfixes from supported release branches (implicitly checking for merge conflicts)
 * run checkbashisms on each shell script
 * run podchecker on each pod file
 * generate and archive a release tarball
 * "make distcheck"
 * build collectd on various environments
 * generate static-analyser report(s)

Configuration generated automatically, do not edit!
""")
  label('master')

  concurrentBuild(false)

  scm {
    git {
      remote {
        name('origin')
        url("https://github.com/${githubOrg}/collectd.git")
        refspec('+refs/heads/*:refs/remotes/origin/*')
        branch('origin/master')
        branch('origin/collectd-*')
        github("${githubOrg}/collectd")
      }
    }
  }

  wrappers {
    environmentVariables {
      envs([
        BUILD_GIT_COMMIT: '$GIT_COMMIT',
      ])
    }
  }

  downstreamProperties = [
    COLLECTD_BUILD:       '$COLLECTD_BUILD',
    BUILD_GIT_COMMIT:     '$BUILD_GIT_COMMIT',
    TARBALL:              '$TARBALL',
    TARBALL_BUILD_NUMBER: '$TARBALL_BUILD_NUMBER',
    UPSTREAM_JOB_NAME:    'master-prepare-tarball',
  ]

  if (setupGithubHooks == 'true') {
    configure { project ->
      project / builders / 'com.cloudbees.jenkins.GitHubSetCommitStatusBuilder'
    }
    triggers {
      githubPush()
    }
  }

  steps {
    shell('/var/lib/jenkins/scripts/cleanup-build-area.sh')
    shell('''# merge the 2 last release branches into master
test -n "$BUILD_NUMBER"
git branch "build-${BUILD_NUMBER}" origin/master
git checkout -f "build-${BUILD_NUMBER}"

BRANCHES="$(git branch --list --all | grep -E 'origin/collectd-[0-9\\.]+$' | sort -t '.' -k 1,1 -k 2,2n | tail -n 2)"
for branch in $BRANCHES; do
  git merge --ff --no-edit --log $branch || (git diff && exit 1)
done

git show --stat HEAD
''')
    shell('/var/lib/jenkins/scripts/check-bashisms.sh')
    shell('/var/lib/jenkins/scripts/check-pod.sh')
    shell('/var/lib/jenkins/scripts/prepare-tarball.sh')

    environmentVariables {
      propertiesFile('${WORKSPACE}/env-${BUILD_GIT_COMMIT}.sh')
    }

    phase('prepare release tarball', 'SUCCESSFUL') {
      job('master-prepare-tarball') {
        props(downstreamProperties)
      }
    }

    shell('make -s distcheck')

    environmentVariables {
      propertiesFile('${JENKINS_HOME}/jobs/master-prepare-tarball/workspace/env-${BUILD_GIT_COMMIT}.sh')
    }

    // NB: unforunately "phase" blocks don't support groovy iterators, so this
    // forces us to file all the jobs manually here.

    phase('touchstone (won\'t continue further down if this step fails)', 'SUCCESSFUL') {
      job('master-build-on-jessie-amd64-with-default-toolchain') {
        props(downstreamProperties)
      }
    }

    phase('mandatory (platforms for which packages are built)', 'SUCCESSFUL') {
      job('master-build-on-jessie-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-xenial-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-xenial-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-trusty-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-trusty-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-precise-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-precise-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-wheezy-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-wheezy-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-epel7-x86_64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-epel6-x86_64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-epel6-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-epel5-x86_64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-epel5-i386-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
    }

    phase('supported (platforms known to work that new patches shouldn\'t break)', 'SUCCESSFUL') {
      job('master-build-on-freebsd10-amd64-with-default-toolchain') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-xenial-amd64-with-clang') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-jessie-amd64-with-libstatgrab') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-wheezy-amd64-with-libstatgrab') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-epel6-x86_64-with-libstatgrab') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-freebsd10-amd64-with-libstatgrab') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
    }

    phase('informative (build options and environments which are know to fail)', 'ALWAYS') {
      job('master-build-on-xenial-amd64-with-clang-strict') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
      job('master-build-on-jessie-amd64-with-musl-libc') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
    }

    phase('static analysis', 'SUCCESSFUL') {
      job('master-build-on-xenial-amd64-with-scan-build') {
        killPhaseCondition('NEVER')
        props(downstreamProperties)
      }
    }
  }
}

job('master-prepare-tarball') {
  displayName('Prepare tarball for testing master')
  description("""
This job simply archives the release tarball from the upstream project.

Configuration generated automatically, do not edit!
""")
  label('master')

  configure { project ->
    project / 'properties' << 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' { }
    project / 'properties' / 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' << 'projectNameList' {
      string 'master-build-on-*'
    }
  }

  steps {
    shell('''
test -n "$BUILD_GIT_COMMIT"
test -n "$TARBALL"
rm -f collectd*.tar.gz env.sh env-*.sh
cp "$JENKINS_HOME/jobs/master-aggregation/workspace/$TARBALL" .
cat > env-${BUILD_GIT_COMMIT}.sh << EOF
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
