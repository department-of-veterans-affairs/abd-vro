FROM ruby:3.3.0-alpine3.19

 # Bundler needs git, and ruby-dev curl-dev build-base to make native gems
 # https://www.cloudbees.com/blog/build-minimal-docker-container-ruby-apps

# hadolint ignore=DL3018
RUN apk update && apk upgrade && apk --no-cache add git ruby-dev=3.2.2-r1 curl-dev build-base=0.5-r3 openssl && rm -rf /var/cache/apk/*

WORKDIR /app

# Copy and install Gems first (without application code)
# b/c this is a big layer that doesn't change often
# See https://vsupalov.com/5-tips-to-speed-up-docker-build/
COPY Gemfile Gemfile.lock ./

# After install, delete packages only needed during bundler install & clean APK cache
RUN gem install bundler:2.5.4 --no-document && bundle install \
    && gem update cgi \
    && apk del git ruby-dev curl-dev build-base \
    && rm -rf /var/cache/apk/*

# https://stackoverflow.com/a/46801962
# Copy script that runs docker-entrypoint.sh if it exists
COPY set-env-secrets.src entrypoint-wrapper.sh docker-entry*.sh ./

# === Image layers above this line are general and will be reused as cache when building the image.
# Put commands that produce project-specific image layers below this line.

RUN adduser --disabled-password --gecos "" tron
COPY . .
RUN chmod +x ./*.sh && chown -R tron /app

# Copy health check scripts
COPY healthcheck/liveness_script.rb /app/healthcheck/liveness_script.rb
COPY healthcheck/readiness_script.rb /app/healthcheck/readiness_script.rb
COPY healthcheck/startup_script.rb /app/healthcheck/startup_script.rb

# Make scripts executable
RUN chmod +x /app/healthcheck/liveness_script.rb \ 
    && chmod +x /app/healthcheck/readiness_script.rb \
    && chmod +x /app/healthcheck/startup_script.rb

USER tron
ENTRYPOINT ["/app/entrypoint-wrapper.sh"]

ARG HEALTHCHECK_PORT_ARG=8080
ENV HEALTHCHECK_PORT=${HEALTHCHECK_PORT_ARG}
HEALTHCHECK CMD bundle exec ruby healthcheck/healthcheck.rb
