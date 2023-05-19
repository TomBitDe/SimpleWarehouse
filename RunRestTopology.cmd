echo off

curl -X OPTIONS -i http://localhost:8080/war/resources/TopologyRestService/Options
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/TopologyRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -X POST -i http://localhost:8080/war/resources/TopologyRestService/SampleData
echo=
echo ----------------------------------------------------------------------

curl -X DELETE -i http://localhost:8080/war/resources/TopologyRestService/SampleData
echo=
echo ----------------------------------------------------------------------


pause