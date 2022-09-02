FROM alpine:3.7
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro

RUN apk --no-cache add curl==7.61.1-r3
HEALTHCHECK NONE
ENTRYPOINT [ "/usr/local/bin/install-policy.sh" ]
CMD [ "--file", "/policy-header.rego", "--glob", "/*.policy.rego" ]
COPY install-policy.sh /usr/local/bin
COPY policy-header.rego /
COPY *policy.rego /
