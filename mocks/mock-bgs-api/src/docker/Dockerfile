FROM castlemock/castlemock:v1.63

#copy the mock definition file, our entrypoint and helper script to load the file
COPY bgs-castlemock.xml entrypoint.sh init-after-tomcat-starts.sh /
RUN chmod a+x /entrypoint.sh /init-after-tomcat-starts.sh

ENTRYPOINT ["/entrypoint.sh"]
