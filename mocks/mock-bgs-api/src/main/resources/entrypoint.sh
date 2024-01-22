#!/bin/bash

#turn on bash job control
set -m

#Start the helper process
/init-after-tomcat-starts.sh &

#Run castlemock app
catalina.sh run

