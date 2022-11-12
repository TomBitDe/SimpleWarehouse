echo off

set PAYARA_HOME=C:\Users\Tom\Programme\Payara_Server\payara5

dir %PAYARA_HOME%\glassfish\domains\domain1\logs
echo=
del %PAYARA_HOME%\glassfish\domains\domain1\logs\*.log*
echo=
dir %PAYARA_HOME%\glassfish\domains\domain1\logs
echo=
dir .\logs
echo=
del .\logs\*.log*
echo=
dir .\logs
