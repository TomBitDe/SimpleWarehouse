echo off

curl -v -X OPTIONS -i http://localhost:8080/war/resources/ApplConfigRestService/Options
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Exists/aaa
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Count
echo=
echo ----------------------------------------------------------------------

curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
echo=
echo ----------------------------------------------------------------------

curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa/Test1
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
echo=
echo ----------------------------------------------------------------------

curl -v -X POST -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa/TestX
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
echo=
echo ----------------------------------------------------------------------

curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/bbb
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ccc
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ddd

curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa/Test1
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/bbb/Test2
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ccc/Test3
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ddd/Test4

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content
curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Count

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content/0/1
curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content/2/2
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Refresh
echo=
echo ----------------------------------------------------------------------

pause