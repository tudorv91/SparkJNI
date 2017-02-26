from fabric.api import *

def clone_repo():
    run("cd ~")
    run("sudo add-apt-repository ppa:openjdk-r/ppa")
    run("sudo apt-get update")
    run("sudo apt-get install git")
    run("sudo apt-get install maven")
    run("sudo apt-get install openjdk-8-jdk")
    run("sudo apt-get install g++")
    # run("git clone https://github.com/tudorv91/SparkJNI.git")
    # time.sleep( 1 )
    with cd("/home/vagrant/SparkJNI"):
        run("git checkout generator")
        run("mvn clean install")