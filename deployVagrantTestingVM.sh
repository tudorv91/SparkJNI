#!/bin/bash
function checkVirtualBox() {
    if ! hash virtualBox 2>/dev/null; then
        echo "VirtualBox is not installed. Install Virtual Box and run the script again."
        exit
    fi
}

function checkVagrant() {
    if ! hash vagrant 2>/dev/null; then
        echo "Vagrant is not installed. Install Vagrant  and run the script again. It is recommended to install Vagrant from the official website and not through the package manager."
        exit
    fi
}

function deploy() {
    vagrant plugin install vagrant-fabric
    cd vagrant
    vagrant up
    vagrant provision
}

checkVirtualBox
checkVagrant
deploy