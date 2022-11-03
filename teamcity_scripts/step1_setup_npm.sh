#!/bin/bash

# 1 arg - build number teamcity
# 2 arg - if test build then  char t

if [ -z "$1" ]
then
echo Build number is undefined
exit -1
fi

# setup npm configuration for npm run build on stage Maven build
HOME_TEAMCITY=/home/teamcity
touch $HOME_TEAMCITY/.npmrc
echo "registry = https://repo.corp.tander.ru/repository/npmjs/" > $HOME_TEAMCITY/.npmrc
echo "strict-ssl = false" >> $HOME_TEAMCITY/.npmrc
mkdir -p $HOME_TEAMCITY/.npm

# calculate version and set teamcity parameter
MAGREPORT_VERSION=$(cat ./version)
MAGREPORT_BUILD_NUMBER=$1
REACT_APP_VERSION=$(echo $MAGREPORT_VERSION).$(echo $MAGREPORT_BUILD_NUMBER)$2

echo "##teamcity[setParameter name='env.REACT_APP_VERSION' value='$REACT_APP_VERSION']"