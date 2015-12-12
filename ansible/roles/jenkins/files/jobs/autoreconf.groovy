job('jenkins-autoreconf') {
  displayName("Reconfigure jenkins jobs when definitions are updated")

  label('master')
  concurrentBuild(false)

  scm {
    git {
      remote {
        name('origin')
        url("https://github.com/collectd/collectd-ci.git")
        branch('origin/master')
        github("collectd/collectd-ci")
      }
    }
  }

  triggers {
    githubPush()
  }

  steps {
    shell('''
test -d "${WORKSPACE}/ansible/roles/jenkins/files/scripts/"
test -d "${WORKSPACE}/ansible/roles/jenkins/files/jobs/"
rsync -rltgoD -v --delete "${WORKSPACE}/ansible/roles/jenkins/files/scripts/" /var/lib/jenkins/scripts/
rsync -rltgoD -v --delete "${WORKSPACE}/ansible/roles/jenkins/files/jobs/" /var/lib/jenkins/jobs/Job_DSL_seed/workspace/
''')
  }

  publishers {
    downstream('Job_DSL_seed')
  }
}
