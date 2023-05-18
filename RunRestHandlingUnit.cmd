echo off

curl -X OPTIONS -i http://localhost:8080/war/resources/HandlingUnitRestService/Options
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/HandlingUnitRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/HandlingUnitRestService/Content
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/HandlingUnitRestService/Content/0/10
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/HandlingUnitRestService/Exists/1
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/HandlingUnitRestService/Exists/2
echo=
echo ----------------------------------------------------------------------

curl -X PUT -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/3
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/HandlingUnitRestService/Exists/3
echo=
echo ----------------------------------------------------------------------

curl -X DELETE -i http://localhost:8080/war/resources/HandlingUnitRestService/Entry/3
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/HandlingUnitRestService/Exists/3
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/HandlingUnitRestService/Count
echo=
echo ----------------------------------------------------------------------


pause