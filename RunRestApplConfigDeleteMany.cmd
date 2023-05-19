echo off

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/bbb
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ccc
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ddd
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/eee
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/fff
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ggg
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/hhh
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/jjj
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/kkk
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/lll
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/mmm
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/nnn
curl -v -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ooo

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content
curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Count
echo=
echo ----------------------------------------------------------------------

curl -v -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Refresh
echo=
echo ----------------------------------------------------------------------

pause