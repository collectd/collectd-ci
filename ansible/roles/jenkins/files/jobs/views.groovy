listView('packages') {
  jobs {
    regex('packages-.+')
  }

  columns {
    name()
    status()
    lastSuccess()
    lastFailure()
    lastDuration()
    lastBuildConsole()
  }
}

listView('pull requests') {
  jobs {
    regex('pull-requests-.+')
  }

  columns {
    name()
    status()
    lastSuccess()
    lastFailure()
    lastDuration()
    lastBuildConsole()
  }
}
