# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.box = "deb/jessie-amd64"
  config.vm.host_name = "collectd-ci-test"

  config.vm.provision "ansible" do |ansible|
      ansible.sudo = true
      ansible.playbook = "ansible/site.yml"
      ansible.groups = {
        "vagrant" => ["default"],
      }
  end

end
