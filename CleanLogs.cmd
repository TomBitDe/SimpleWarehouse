echo off

IF "%PAYARA_HOME%"=="" GOTO NOT_DEFINED 

:CLEAN_DOMAINS
dir %PAYARA_HOME%\glassfish\domains\domain1\logs
echo=
del %PAYARA_HOME%\glassfish\domains\domain1\logs\*.log*
echo=
dir %PAYARA_HOME%\glassfish\domains\domain1\logs
echo=
GOTO CLEAN_LOGS

:NOT_DEFINED
echo PAYARA_HOME is NOT defined

:CLEAN_LOGS
dir .\logs
echo=
del .\logs\*.log*
del .\logs\*.xml
echo=
dir .\logs
