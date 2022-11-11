if test -z "$MAGREPORT_HOME" 
then
    echo "Please, set MAGREPORT_HOME environmental variable"
    exit 1
fi
export REACT_APP_VERSION=`cat version`
mvn package
cp -f .\magreport-backend\target\magreport-backend-2.0.jar $MAGREPORT_HOME\magreport.jar
cp -f .\magreport-backend\run.sh $MAGREPORT_HOME\run.sh
cp -f .\magreport-backend\run-magreport-admin.sh $MAGREPORT_HOME\run-magreport-admin.sh
cp -f .\version $MAGREPORT_HOME\version
cp -f .\docs\user-manual\src\user-manual.pdf $MAGREPORT_HOME\user-manual.pdf
cp -f .\magreport-admin\target\magreport-admin-0.0.1-SNAPSHOT.jar $MAGREPORT_HOME\magreport-admin.jar