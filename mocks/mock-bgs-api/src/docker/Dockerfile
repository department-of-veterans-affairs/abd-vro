FROM castlemock/castlemock:v1.65

# 8080 is the default port that spring-actuator uses
ARG HEALTHCHECK_PORT_ARG=8080
ENV HEALTHCHECK_PORT=${HEALTHCHECK_PORT_ARG}


#copy the mock definition file, our entrypoint and helper script to load the file
COPY bgs-castlemock.xml entrypoint.sh init-after-tomcat-starts.sh /
RUN chmod a+x /entrypoint.sh /init-after-tomcat-starts.sh

ENTRYPOINT ["/entrypoint.sh"]
