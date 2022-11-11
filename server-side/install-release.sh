#!/bin/bash

version=$(cat ./package/var/magreport2/version | tr -d [:space:])
rpmfile=$(find ./rpmbuild/RPMS/x86_64/magreport2-$version-*)

if [ ${#rpmfile} -eq 0 ]; then
    echo Release rpm file not found
    exit -1
fi

echo Stopping service...
sudo service magreport2 stop
sudo service magreport-admin stop

if [ $? -eq 0 ]; then
    echo Backing up magreport database...
    ./make-db-backup.sh
else
    echo Service stopping failed
    exit -1
fi

if [ $? -eq 0 ]; then
    echo Upgrading package...
    sudo yum -y upgrade $rpmfile
else
    echo DB backup FAILED. Upgrade didnt install.
    exit -1
fi

if [ $? -eq 0 ]; then
    echo Starting service...
    sudo service magreport-admin start
    sudo service magreport2 start
else
    echo Package upgrade failed.
    exit -1
fi

