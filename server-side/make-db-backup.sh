#!/bin/bash

# magreport db folder
MAGREP2_DB_FOLDER=/var/magreport2/db

# backup folder
#BKP_FOLDER=/bkp/magreport2/db
BKP_FOLDER=./db-backup

# archive file name
ARCH_FILENAME=magrep2_db_bkp_$(date +'%Y-%m-%d-%H-%M-%S').tar.gz


# remove old backups
#find $BKP_FOLDER -type f -mtime +32 -exec rm -f {} \;

# create archive
tar -zcvf $BKP_FOLDER/$ARCH_FILENAME -C $MAGREP2_DB_FOLDER .




