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

listView('tests') {
  jobs {
    regex('.+-aggregation')
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
