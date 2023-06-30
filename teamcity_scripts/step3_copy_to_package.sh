#! /bin/bash

# 1 arg - build dir path
# 2 arg - %env.REACT_APP_VERSION%
# 3 arg - prod or test

cd  $1
rm -rf ./package
mkdir -p ./package

mkdir -p ./package/etc/sysconfig
cp -f ./config/package/etc/sysconfig/magreport2 ./package/etc/sysconfig/magreport2

mkdir -p ./package/usr/lib/systemd/system
cp -f ./config/package/usr/lib/systemd/system/magreport2_$3.service ./package/usr/lib/systemd/system/magreport2.service 

mkdir -p ./package/var/log/magreport2
touch ./package/var/log/magreport2/magreport2.log

mkdir -p ./package/var/magreport2/excel-templates
cp -rf  ./magreport-backend/src/main/resources/templates/* ./package/var/magreport2/excel-templates

mkdir -p ./package/var/magreport2
cp -rf ./config/package/var/magreport2/keystore ./package/var/magreport2

mkdir -p ./package/var/magreport2/jdbc
cp -rf ./magreport-backend/jdbc/* ./package/var/magreport2/jdbc

cp -f ./config/package/var/magreport2/application_$3.properties ./package/var/magreport2/application.properties

cp -f ./config/package/var/magreport2/logback.xml ./package/var/magreport2/logback.xml

cp -f ./magreport-backend/target/magreport-backend-2.1.jar ./package/var/magreport2/magreport2.jar

echo $2 > ./package/var/magreport2/version

# magreport-admin

cp -f ./config/package/etc/sysconfig/magreport-admin ./package/etc/sysconfig/magreport-admin
cp -f ./config/package/usr/lib/systemd/system/magreport-admin.service ./package/usr/lib/systemd/system/magreport-admin.service

mkdir -p ./package/var/magreport2/magreport-admin
cp -f ./magreport-admin/target/magreport-admin-0.0.1-SNAPSHOT.jar ./package/var/magreport2/magreport-admin/magreport-admin.jar
cp -f ./config/package/var/magreport2/magreport-admin/application_$3.properties ./package/var/magreport2/magreport-admin/application.properties

