echo off

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/aaa
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/bbb
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ccc
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ddd
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/eee
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/fff
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ggg
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/hhh
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/jjj
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/kkk
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/lll
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/mmm
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/nnn
curl -X DELETE -i http://localhost:8080/war/resources/ApplConfigRestService/Entry/ooo

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Content
curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Count
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/resources/ApplConfigRestService/Refresh
echo=
echo ----------------------------------------------------------------------

pause