FROM alpine:3


# hadolint ignore=DL3018
RUN apk update && \
    apk --no-cache add curl redis sudo && \
    apk --no-cache add postgresql15-client && \
    apk upgrade openssl libssl3 libcrypto3 && \
    rm -rf /var/cache/apk/*

# SecRel requires that containers not run as root
USER dev

# This will cause the container to sleep for 10 hours before automatically terminating.
# This will prevent the container from lingering in our LHDI environments
ENTRYPOINT ["/bin/sleep", "3600"]
