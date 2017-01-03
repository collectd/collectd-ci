def branches = ['master', 'collectd-5.6', 'collectd-5.7']
branches.each {
  def branchName = "${it}"

  job("packages-prepare-tarball-${branchName}") {
    displayName("Prepare tarball for building packages (${branchName} branch)")
    description("""
This job generates a release tarball out of the '${branchName}' branch and archives it for downstream consumption.

Configuration generated automatically, do not edit!
""")
    blockOnDownstreamProjects()

    label('master')

    scm {
      git {
        remote {
          name('origin')
          url('https://github.com/collectd/collectd.git')
        }
        branch("origin/${branchName}")
      }
    }

    triggers {
      scm('@hourly')
    }

    configure { project ->
      project / 'properties' << 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' { }
      project / 'properties' / 'hudson.plugins.copyartifact.CopyArtifactPermissionProperty' << 'projectNameList' {
        string 'packages-make-*'
      }
    }

    wrappers {
      environmentVariables {
        envs([
          BUILD_GIT_BRANCH: '$GIT_BRANCH',
          BUILD_GIT_COMMIT: '$GIT_COMMIT',
        ])
      }
    }

    steps {
      shell('/var/lib/jenkins/scripts/cleanup-build-area.sh')
      shell('/var/lib/jenkins/scripts/prepare-tarball.sh')
      shell('git show "${GIT_BRANCH}:contrib/redhat/collectd.spec" > collectd.spec')
      shell('mv env-${BUILD_GIT_COMMIT}.sh env.sh')
    }

    publishers {
      archiveArtifacts {
        pattern('collectd*.tar.gz')
        pattern('env.sh')
        pattern('collectd.spec')
      }
      downstream("packages-make-deb-${branchName}", 'SUCCESS')
      downstream("packages-make-rpm-${branchName}", 'SUCCESS')
    }
  }

  matrixJob("packages-make-deb-${branchName}") {
    displayName("Build packages for Debian/Ubuntu LTS (${branchName} branch)")
    description("""
This job:
 * extracts the tarball passed down from the 'packages-prepare-tarball-${branchName}' job
 * builds .deb packages for various distros
 * pushes the result to the repository hosted at http://ci.collectd.org/

Configuration generated automatically, do not edit!
""")
    runSequentially(true)

    label('master')

    configure { project ->
      project / 'properties' << 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' {
        maxConcurrentPerNode 1
        maxConcurrentTotal 1
        throttleEnabled true
        throttleOption 'category'
      }
      project / 'properties' / 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' << 'categories' {
        string 'pbuilder'
      }
      project / 'properties' / 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' << 'matrixOptions' {
        throttleMatrixBuilds true
        throttleMatrixConfigurations false
      }
    }

    axes {
      text('distro', 'precise', 'trusty', 'xenial', 'wheezy', 'jessie')
      text('arch', 'i386', 'amd64')
      label('buildhost', 'master')
    }

    steps {
      copyArtifacts("packages-prepare-tarball-${branchName}") {
        includePatterns('collectd*.tar.gz', 'env.sh')
        buildSelector {
          upstreamBuild(true)
        }
      }

      shell('/var/lib/jenkins/scripts/make-debs.sh $distro $arch')
      shell('/var/lib/jenkins/scripts/s3-apt-repo.sh')
      shell('/var/lib/jenkins/scripts/update-apt-release.sh')
    }

    publishers {
      downstream('packages-sync-repos')
    }
  }

  matrixJob("packages-make-rpm-${branchName}") {
    displayName("Build packages for CentOS/EPEL (${branchName} branch)")
    description("""
This job:
 * extracts the tarball passed down from the 'packages-prepare-tarball-${branchName}' job
 * builds .rpm packages for various distros
 * pushes the result to the repository hosted at http://ci.collectd.org/

Configuration generated automatically, do not edit!
""")
    runSequentially(true)

    label('master')

    configure { project ->
      project / 'properties' << 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' {
        maxConcurrentPerNode 1
        maxConcurrentTotal 1
        throttleEnabled true
        throttleOption 'category'
      }
      project / 'properties' / 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' << 'categories' {
        string 'mock'
      }
      project / 'properties' / 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' << 'matrixOptions' {
        throttleMatrixBuilds true
        throttleMatrixConfigurations false
      }
    }

    axes {
      text('distro', 'epel-6-i386', 'epel-6-x86_64', 'epel-7-x86_64')
      label('buildhost', 'master')
    }

    steps {
      copyArtifacts("packages-prepare-tarball-${branchName}") {
        includePatterns('collectd*.tar.gz', 'env.sh', 'collectd.spec')
        buildSelector {
          upstreamBuild(true)
        }
      }

      shell('/var/lib/jenkins/scripts/make-rpms.sh $distro')
      shell('/var/lib/jenkins/scripts/s3-yum-repo.sh')
    }

    publishers {
      downstream('packages-sync-repos')
    }
  }
}

job('packages-sync-repos') {
  displayName("Update packages repositories")
  description("""
This job pulls down packages archived on S3, triggered when some upstream package-building task finishes.

Configuration generated automatically, do not edit!
""")
  concurrentBuild(false)
  label('pkg')
  steps {
    shell('''
s3cmd --delete-removed --exclude pubkey.asc sync s3://collectd/ /srv/repos/
find /srv/repos/deb/ /srv/repos/rpm/ -name status.json | xargs jq --slurp '{ status: . }' > /srv/repos/status.new
mv /srv/repos/status.new /srv/repos/status.json
''')
  }
}
