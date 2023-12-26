echo off

curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/C
curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/D
curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/E
curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/F
curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/G
curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/H
curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/J
curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/K
curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/L
REM curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/M
REM curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/N
REM curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/O
REM curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/P
REM curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/Q
REM curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/R

curl -v -X PUT -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/3
curl -v -X PUT -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/4
curl -v -X PUT -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/5
curl -v -X PUT -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/6
curl -v -X PUT -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/7
curl -v -X PUT -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/8
curl -v -X PUT -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/9
curl -v -X PUT -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/10

curl -v -X POST -i http://localhost:8080/war/resources/HandlingUnitRestService/Drop/C/3
curl -v -X POST -i http://localhost:8080/war/resources/HandlingUnitRestService/Drop/E/5
curl -v -X POST -i http://localhost:8080/war/resources/HandlingUnitRestService/Drop/E/6
curl -v -X POST -i http://localhost:8080/war/resources/HandlingUnitRestService/Drop/E/7
curl -v -X POST -i http://localhost:8080/war/resources/HandlingUnitRestService/Drop/F/10
curl -v -X POST -i http://localhost:8080/war/resources/HandlingUnitRestService/Drop/F/9

echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/LocationRestService/Count
echo=
echo ----------------------------------------------------------------------
curl -v -X GET -i http://localhost:8080/war/resources/HandlingUnitRestService/Count
echo=
echo ----------------------------------------------------------------------

pause