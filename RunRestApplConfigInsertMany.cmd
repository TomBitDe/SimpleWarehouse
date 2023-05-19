echo off

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa/Test1
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/bbb/Test2
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ccc/Test3
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ddd/Test4
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/eee/Test5
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/fff/Test6
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ggg/Test7
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/hhh/Test8
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/jjj/Test9
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/kkk/Test10
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/lll/Test11
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/mmm/Test12
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/nnn/Test13
curl -v -X PUT -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ooo/Test14

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content
curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Count
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Refresh
echo=
echo ----------------------------------------------------------------------

pause