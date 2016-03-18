# Continuous Integration for Collectd

The goal is to automate building and testing changes to
[collectd's git repository](https://github.com/collectd/collectd/).

## Using

### Integration with Linux distros

Packages are built and pushed to a repository within a couple of hours each
time new commits are added to the following branches:

* ["master"](https://github.com/collectd/collectd/tree/master/) (feature branch
  which will become the next point release)
* ["collectd-5.5"](https://github.com/collectd/collectd/tree/collectd-5.5)
  (release branch including only bugfixes)
* ["collectd-5.4"](https://github.com/collectd/collectd/tree/collectd-5.4)
  (release branch including only bugfixes)

The status of these builds can be tracked in [Jenkins](https://ci.collectd.org/view/packages/).

The package and repository signing key can be double-checked on [Github](https://raw.githubusercontent.com/collectd/collectd-ci/master/ansible/roles/pkgrepo/files/pubkey.asc) and on [PGP keyservers](http://pgp.mit.edu/pks/lookup?op=vindex&search=0x3994D24FB8543576).

**RHEL/CentOS**

Add the following to `/etc/yum.repos.d/collectd-ci.repo`:

```
[collectd-ci]
name=collectd CI
baseurl=http://pkg.ci.collectd.org/rpm/<branch>/epel-<rel>-$basearch
enabled=1
gpgkey=http://pkg.ci.collectd.org/pubkey.asc
gpgcheck=1
repo_gpgcheck=1
```

Replace `<branch>` with one of the branches mentioned above. Replace `<rel>`
with one of:

* 5
* 6
* 7

Both i386 and x86\_64 architecture are supported, except for EL7 (no i386).
EL5 users will need to set `gpgcheck` and `repo_gpgcheck` to 0, as this
distribution is too old to support the GPG key format the repository uses.

**Debian/Ubuntu-LTS**

Import the repository signing key:

```
curl http://pkg.ci.collectd.org/pubkey.asc | apt-key add -
```

Then add the following to `/etc/apt/sources.list.d/collectd-ci.list`.

```
deb http://pkg.ci.collectd.org/deb/ <codename> <branch>
```

Replace `<branch>` with one of the branches mentioned above. Replace
`<codename>` with one of:

* precise
* trusty
* wheezy
* jessie

Only i386 and amd64 architectures are supported.

### Github Pull Requests

Each pull-request is merged into the master branch, and then built on a range
of platforms.

The default compiler on each platform will be used, and where relevant, with
the build options used when building packages for this platform. The support
libraries used are those found by default on each platform.

The status of these builds is visible on [collectd's pull requests page](https://github.com/collectd/collectd/pulls).

### Master and release branches

Each time a new patch is pushed to the master or release branches, it runs
through various checks, which currently include:

* checking for merge-conflicts with the release branches
* ensuring the build system is able to able to make a release
* building the code on various platforms (mostly the same checks as for
  pull-requests)
* generating static-analysis report(s)

The status of these builds can be tracked in [Jenkins](https://ci.collectd.org/job/master-aggregation/).

## Contributing

Feedback, suggestions and improvements are very welcome. Please use this
project's issues, the [collectd
mailing-list](https://collectd.org/wiki/index.php/Mailing_list) or the
[#collectd](http://webchat.freenode.net/?channels=collectd) IRC channel.

Please report issues *using* collectd to the
[main collectd repository](https://github.com/collectd/collectd/).

Feel free to browse through this repo. The build scripts are located in
[ansible/roles/buildenv/files/](https://github.com/collectd/collectd-ci/tree/master/ansible/roles/buildenv/files)
and the job descriptions are in [ansible/roles/jenkins/files/jobs](https://github.com/collectd/collectd-ci/tree/master/ansible/roles/jenkins/files/jobs). A `Vagrantfile` is provided to help getting a local Jenkins instance running.

## Thanks!

[Exoscale](https://www.exoscale.ch/) is offering the compute resources for
running the main CI infrastructure.
[DigitalOcean](https://www.digitalocean.com/) is offering on-demand virtual
machines for the build jobs. Thanks a lot!
