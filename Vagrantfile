# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.box = "deb/jessie-amd64"
  config.vm.host_name = "collectd-ci-test"
  config.vm.network "forwarded_port", guest: 8080, host: 8088

  config.vm.provider "virtualbox" do |v|
       v.memory = 1024
   end

  config.vm.provision "ansible" do |ansible|
      ansible.sudo = true
      ansible.playbook = "ansible/ci.yml"
      ansible.groups = {
        "vagrant" => ["default"],
      }
  end

end
