#!/bin/bash

until [ "`curl --silent --show-error --connect-timeout 1 -I http://localhost:8080/castlemock/ | grep 'HTTP/1.1 200'`" != "" ];
do
  echo --- waiting on castlemock for 3 seconds
  sleep 3
done

echo castlemock is ready!

curl -X POST "http://localhost:8080/castlemock/api/rest/core/project/soap/import" -H "accept: /" -H "Authorization: Basic YWRtaW46YWRtaW4=" -H "Content-Type: multipart/form-data" -F "file=@/bgs-castlemock.xml;type=text/xml"

