echo off

curl -v -X OPTIONS -i http://localhost:8080/war/resources/TopologyRestService/Options
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/TopologyRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -v -X POST -i http://localhost:8080/war/resources/TopologyRestService/SampleData
echo=
echo ----------------------------------------------------------------------

curl -v -X DELETE -i http://localhost:8080/war/resources/TopologyRestService/SampleData
echo=
echo ----------------------------------------------------------------------


pause