def branches = ['master', 'collectd-5.5', 'collectd-5.4']
branches.each {
  def branchName = "${it}"

  job("make-dist-tarball-${branchName}") {
    displayName("prepare tarball for deb/rpm packages (${branchName} branch)")
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

    steps {
      shell('/usr/local/bin/make-dist-archive.sh $GIT_COMMIT')
    }

    publishers {
      archiveArtifacts {
        pattern('collectd*.tar.bz2')
        pattern('env.sh')
        pattern('collectd.spec')
      }
      downstream("make-deb-pkgs-${branchName}", 'SUCCESS')
      downstream("make-rpm-pkgs-${branchName}", 'SUCCESS')
    }
  }

  matrixJob("make-deb-pkgs-${branchName}") {
    displayName("build deb packages for Debian/Ubuntu LTS (${branchName} branch)")
    description("""
This job:
 * extracts the tarball passed down from the 'make-dist-tarball-${branchName}' job
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
      text('distro', 'precise', 'trusty', 'squeeze', 'wheezy', 'jessie')
      text('arch', 'i386', 'amd64')
      label('buildhost', 'master')
    }

    steps {
      copyArtifacts("packages-prepare-tarball-${branchName}") {
        includePatterns('collectd*.tar.bz2', 'env.sh')
        buildSelector {
          upstreamBuild(true)
        }
      }

      shell('/usr/local/bin/make-debs.sh $distro $arch')
      shell('/usr/local/bin/s3-apt-repo.sh')
      shell('/usr/local/bin/update-apt-release.sh')
    }

    publishers {
      downstream('sync-package-repos')
    }
  }

  matrixJob("make-rpm-pkgs-${branchName}") {
    displayName("build rpm packages for CentOS/EPEL (${branchName} branch)")
    description("""
This job:
 * extracts the tarball passed down from the 'make-dist-tarball-${branchName}' job
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
      text('distro', 'epel-5-i386', 'epel-5-x86_64', 'epel-6-i386', 'epel-6-x86_64', 'epel-7-x86_64')
      label('buildhost', 'master')
    }

    steps {
      copyArtifacts("packages-prepare-tarball-${branchName}") {
        includePatterns('collectd*.tar.bz2', 'env.sh', 'collectd.spec')
        buildSelector {
          upstreamBuild(true)
        }
      }

      shell('/usr/local/bin/make-rpms.sh $distro')
      shell('/usr/local/bin/s3-yum-repo.sh')
    }

    publishers {
      downstream('sync-package-repos')
    }
  }
}

job('sync-package-repos') {
  description("""
This job pulls down packages archived on S3, triggered when some upstream package-building task finishes.

Configuration generated automatically, do not edit!
""")
  concurrentBuild(false)
  label('pkg')
  steps {
    shell('s3cmd --delete-removed --exclude pubkey.asc sync s3://collectd/ /srv/repos/')
  }
}
