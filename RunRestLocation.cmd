echo off

curl -X OPTIONS -i http://localhost:8080/war/resources/LocationRestService/Options
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/LocationRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/LocationRestService/Content
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/LocationRestService/Content/0/10
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/LocationRestService/Exists/A
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/LocationRestService/Exists/B
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/LocationRestService/Count
echo=
echo ----------------------------------------------------------------------


pause