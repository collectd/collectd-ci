# Continuous Integration for Collectd

The goal is to automate building and testing changes to
[collectd's git repository](https://github.com/collectd/collectd/).

## Using

### Integration with Linux distros

Packages are built and pushed to a repository within a couple of hours each
time new commits are added to the following branches:

* ["master"](https://github.com/collectd/collectd/tree/master/) (feature branch
  which will become the next point release)
* ["collectd-5.4"](https://github.com/collectd/collectd/tree/collectd-5.4)
  (release branch including only bugfixes)

#### RHEL/CentOS

Add the following to `/etc/yum.repos.d/collectd-ci.repo`:

```
[collectd-ci]
name=collectd CI
baseurl=http://sos.exo.io/collectd/rpm/<branch>/epel-<rel>-$basearch
enabled=1
gpgcheck=0
```

Replace `<branch>` with one of the branches mentioned above. Replace `<rel>`
with one of:

* 5
* 6
* 7

Both i386 and x86\_64 architecture are supported, except for EL7 (no i386).

#### Debian/Ubuntu-LTS

Add the following to `/etc/apt/sources.list.d/collectd-ci.list`.

```
deb http://sos.exo.io/collectd/deb/ <codename> <branch>
```

Replace `<branch>` with one of the branches mentioned above. Replace
`<codename>` with one of:

* precise
* trusty
* squeeze
* wheezy
* jessie

Only i386 and amd64 architectures are supported.

### Testing patches on various platforms

... work in progress ...

## Contributing

Feedback, suggestions and improvements are very welcome. Please use this
project's issues, the [collectd
mailing-list](https://collectd.org/wiki/index.php/Mailing_list) or the
[#collectd](http://webchat.freenode.net/?channels=collectd) IRC channel.

Please report issues *using* collectd to the
[main collectd repository](https://github.com/collectd/collectd/).

Feel free to browse through this repo. The main build scripts and job
descriptions are located in
[ansible/roles/buildenv/files/](https://github.com/collectd/collectd-ci/tree/master/ansible/roles/buildenv/files).

## Thanks!

[Exoscale](https://www.exoscale.ch/) is offering the compute resources for
running the main CI infrastructure.
[DigitalOcean](https://www.digitalocean.com/) is offering on-demand virtual
machines for the build jobs. Thanks a lot!
