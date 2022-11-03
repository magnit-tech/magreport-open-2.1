#!/bin/bash

#version=0.0.7t
version=$(cat ./package/var/magreport2/version | tr -d [:space:])
datelabel=$(date +"%Y%m%d")

mv ./package ./magreport2-$version

tar -cvzf ./rpmbuild/SOURCES/magreport2-$version.tgz ./magreport2-$version

mv ./magreport2-$version ./package

cat ./rpmbuild/SPECS/magreport2.spec.template | sed "s/#VERSION#/$version/" | sed "s/#DATELABEL#/$datelabel/" > ./rpmbuild/SPECS/magreport2.spec

cd ./rpmbuild
./rpmbuild-magreport2.sh
cd ..
