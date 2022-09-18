echo off

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Ping
echo=
echo ----------------------------------------------------------------------

curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/aaa
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/bbb
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ccc
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ddd
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/eee
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/fff
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ggg
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/hhh
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/jjj
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/kkk
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/lll
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/mmm
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/nnn
curl -X DELETE -i http://localhost:8080/war/rest/ApplConfigRestService/Entry/ooo

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Content
curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Count
echo=
echo ----------------------------------------------------------------------

curl -X GET -i http://localhost:8080/war/rest/ApplConfigRestService/Refresh
echo=
echo ----------------------------------------------------------------------

pause