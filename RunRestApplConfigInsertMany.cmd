echo off

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/aaa/Test1
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/bbb/Test2
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ccc/Test3
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ddd/Test4
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/eee/Test5
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/fff/Test6
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ggg/Test7
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/hhh/Test8
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/jjj/Test9
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/kkk/Test10
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/lll/Test11
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/mmm/Test12
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/nnn/Test13
curl -X PUT -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ooo/Test14

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Content
curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Count
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Refresh
echo=
echo ----------------------------------------------------------------------

pause