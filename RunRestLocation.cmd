echo off

curl -v -X OPTIONS -i http://localhost:8080/war/resources/LocationRestService/Options
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/LocationRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/LocationRestService/Content
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/LocationRestService/Content/0/10
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/LocationRestService/Exists/A
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/LocationRestService/Exists/B
echo=
echo ----------------------------------------------------------------------

curl -v -X PUT -i http://localhost:8080/war/resources/LocationRestService/Entry/C
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/LocationRestService/Exists/C
echo=
echo ----------------------------------------------------------------------

curl -v -X DELETE -i http://localhost:8080/war/resources/LocationRestService/Entry/C
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/LocationRestService/Exists/C
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/LocationRestService/Count
echo=
echo ----------------------------------------------------------------------


pause