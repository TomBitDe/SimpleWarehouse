echo off

curl -X OPTIONS -i http://localhost:8080/war/resources/ApplConfigRestService
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Exists/aaa
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Count
echo=
echo ----------------------------------------------------------------------

curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
echo=
echo ----------------------------------------------------------------------

curl -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa/Test1
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
echo=
echo ----------------------------------------------------------------------

curl -X POST -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa/TestX
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
echo=
echo ----------------------------------------------------------------------

curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/bbb
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ccc
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ddd

curl -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa/Test1
curl -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/bbb/Test2
curl -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ccc/Test3
curl -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ddd/Test4

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content
curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Count

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content/0/1
curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content/2/2
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Refresh
echo=
echo ----------------------------------------------------------------------

pause