echo off

curl -X OPTIONS -i http://localhost:8080/war/rest/ApplConfigRestService
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Content
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Exists/aaa
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Count
echo=
echo ----------------------------------------------------------------------

curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/aaa
echo=
echo ----------------------------------------------------------------------

curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/aaa/Test1
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/aaa
echo=
echo ----------------------------------------------------------------------

curl -X POST -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/aaa/TestX
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/aaa
echo=
echo ----------------------------------------------------------------------

curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/aaa
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/bbb
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ccc
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ddd

curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/aaa/Test1
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/bbb/Test2
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ccc/Test3
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ddd/Test4

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Content
curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Count

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Content/0/1
curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Content/2/2
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Refresh
echo=
echo ----------------------------------------------------------------------

pause