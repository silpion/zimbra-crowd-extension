# -*- mode: ruby -*-
# vi: set ft=ruby :
require 'yaml'

PROVDIR = "src/test/vagrant"

Dir.chdir(File.dirname(__FILE__))
env = YAML::load_file(File.join(PROVDIR, "Vagrantfile.yml"))
if File.exist?("Vagrantfile.yml")
  env.merge!(YAML::load_file("Vagrantfile.yml"))
end

Vagrant.configure("2") do |multi|
  multi.vm.define "zcs", primary: true do |config|
    config.vm.box = env["zcs"]["box"]

    config.vm.hostname = "zcs.zimbra.test"
    config.vm.network "private_network", ip: env["zcs"]["ip4"]

    config.vm.network "forwarded_port", guest:   80, host: 7080
    config.vm.network "forwarded_port", guest:  443, host: 7443
    config.vm.network "forwarded_port", guest: 7071, host: 7071

    config.vm.provider "virtualbox" do |vb|
      vb.memory = env["zcs"]["ram"]
    end

    config.vm.provision "shell", inline: <<-end
      set -ex
      lsb_release -a
      cd /vagrant
      PROVDIR=$PWD/#{PROVDIR}
      export DEBIAN_FRONTEND=noninteractive
      apt-get update

      awk '$2 == "zcs.zimbra.test" { $1 = "#{env["zcs"]["ip4"]}" } { print $0 }' /etc/hosts | tee /etc/hosts.new
      mv /etc/hosts.new /etc/hosts

      apt-get install -y make curl

      apt-get install -y libperl5.18 libaio1 unzip pax sysstat sqlite3

      make -C $PROVDIR VERSION=#{env["zcs"]["version"]}
      test -f $PROVDIR/install.skip || make -C $PROVDIR VERSION=#{env["zcs"]["version"]} install

      install -m 0755 $PROVDIR/zmdo /usr/local/bin/zmdo

      zmdo zmprov -f $PROVDIR/setup.prov || true

      if zmdo zmprov desc | grep -q -F zimbraMtaLmtpHostLookup; then
        zmdo zmprov mcf zimbraMtaLmtpHostLookup native
      else
        zmdo zmlocalconfig -e postfix_lmtp_host_lookup=native
      fi
      zmdo zmmtactl restart
    end
  end

  multi.vm.define "aux" do |config|
    config.vm.box = env["aux"]["box"]

    config.vm.hostname = "aux.zimbra.test"
    config.vm.network "private_network", ip: env["aux"]["ip4"]

    config.vm.network "forwarded_port", guest: 6080, host: 6080

    config.vm.provider "virtualbox" do |vb|
      vb.memory = env["aux"]["ram"]
    end

    config.vm.provision "shell", inline: <<-end
      set -ex
      lsb_release -a
      cd /vagrant
      PROVDIR=$PWD/#{PROVDIR}
      export DEBIAN_FRONTEND=noninteractive
      apt-get update
      
      apt-get install -y postfix
      postconf -e relayhost=#{env["zcs"]["ip4"]}
      postfix reload
    end
  end
end

