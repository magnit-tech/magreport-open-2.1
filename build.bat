@echo off
if not defined MAGREPORT_HOME (
echo Please, set MAGREPORT_HOME environmental variable
exit /B
)
echo MAGREPORT_HOME: %MAGREPORT_HOME%
@echo on

SET /P REACT_APP_VERSION=< version
call mvn package
copy /y .\magreport-backend\target\magreport-backend-2.1.jar %MAGREPORT_HOME%\magreport.jar
copy /y .\version %MAGREPORT_HOME%\version
copy /y .\magreport-backend\run.bat %MAGREPORT_HOME%\run.bat
copy /y .\docs\user-manual\src\user-manual.pdf %MAGREPORT_HOME%\user-manual.pdf
copy /y .\magreport-admin\target\magreport-admin-0.0.1-SNAPSHOT.jar %MAGREPORT_HOME%\magreport-admin\magreport-admin.jar
copy /y .\magreport-admin\run-magreport-admin.bat %MAGREPORT_HOME%\magreport-admin\run-magreport-admin.bat
