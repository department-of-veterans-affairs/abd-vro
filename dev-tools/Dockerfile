FROM bitnami/minideb:bookworm

# hadolint ignore=DL3018,DL3009
RUN apt-get update && \
    install_packages curl sudo && \
    install_packages postgresql-client

# Install AWS CLI v2
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    sudo ./aws/install && \
    rm -rf awscliv2.zip aws

# Verify AWS CLI installation
RUN /usr/local/bin/aws --version

# Create a non-root user named tron
RUN adduser --disabled-password --gecos "" tron && \
    echo 'export PATH=$PATH:/usr/local/bin' >> /home/tron/.profile

# Ensure .profile file is present and sourced in .bashrc
RUN touch /home/tron/.profile && \
    chown tron:tron /home/tron/.profile && \
    echo 'if [ -f ~/.profile ]; then . ~/.profile; fi' >> /home/tron/.bashrc
    
USER tron

# This will cause the container to sleep for 10 hours before automatically terminating.
# This will prevent the container from lingering in our LHDI environments
ENTRYPOINT ["/bin/sleep", "36000"]
